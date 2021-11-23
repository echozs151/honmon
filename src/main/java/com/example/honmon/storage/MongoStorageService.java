/*  
Copyright 2021 the original author or authors.

This file is part of Honmon.

Honmon is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

Honmon is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with Honmon.  If not, see <https://www.gnu.org/licenses/>.
*/
package com.example.honmon.storage;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.stream.Stream;
import java.util.zip.CRC32;
import java.util.zip.CheckedInputStream;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.client.gridfs.model.GridFSFile;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.Resource;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsOperations;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@Primary
public class MongoStorageService implements StorageService<StoredFile> {

    @Autowired
    private GridFsTemplate template;

    @Autowired
    private GridFsOperations operations;

    @Override
    public void init() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public String store(MultipartFile file) {
        

        DBObject metadata = new BasicDBObject();
        metadata.put("fileSize", file.getSize());
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            metadata.put("checksum", md.digest(file.getBytes()).toString());
        } catch (IOException | NoSuchAlgorithmException e1) {
            e1.printStackTrace();
        }

        Object fileID;
        try {
            fileID = template.store(file.getInputStream(), file.getOriginalFilename(), file.getContentType(), metadata);
            return fileID.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    @Override
    public String storeBytes(InputStream input, long length, String name, String contentType) {
        DBObject metadata = new BasicDBObject();
        // CheckedInputStream checkedInputStream = new CheckedInputStream(input, new CRC32());
        // byte[] buffer = new byte[256];
        int size = 0;
        try {
            size = input.available();
            // while (checkedInputStream.read(buffer, 0, buffer.length) >= 0) {}
            // Long chksm = checkedInputStream.getChecksum().getValue();
            metadata.put("fileSize", size);
        } catch (IOException e2) {
            e2.printStackTrace();
        }
        Object fileID;
        fileID = template.store(input, name, contentType, metadata);
        return fileID.toString();
    }

    @Override
    public Stream<StoredFile> loadAll() {
        // return template.find(new Query(Criteria.where("_id").gt(0))).map(mapper -> {new LoadFile();});
        return null;
    }

    @Override
    @Cacheable("file")
    public StoredFile load(String id) throws IOException {
        GridFSFile gridFSFile = template.findOne( new Query(Criteria.where("_id").is(id)) );

        StoredFile loadFile = new StoredFile();

        if (gridFSFile != null && gridFSFile.getMetadata() != null) {
            loadFile.setFilename( gridFSFile.getFilename() );

            loadFile.setFileType( gridFSFile.getMetadata().get("_contentType").toString() );

            loadFile.setFileSize( gridFSFile.getMetadata().get("fileSize").toString() );

            loadFile.setFile( IOUtils.toByteArray(operations.getResource(gridFSFile).getInputStream()) );

            loadFile.setId(gridFSFile.getObjectId());

        }

        return loadFile;
    }

    @Override
    public Resource loadAsResource(String filename) {
        return null;
    }

    @Override
    public void deleteAll() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void delete(String id) {
        template.delete(new Query(Criteria.where("_id").is(id)));
        
    }
    
}
