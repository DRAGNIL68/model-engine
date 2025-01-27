package net.outmoded.modelengine.pack;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;
import net.outmoded.modelengine.pack.jsonObjects.Writable;
import org.apache.commons.io.FileUtils;
import org.bukkit.ChatColor;
import org.jetbrains.annotations.ApiStatus;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import static org.bukkit.Bukkit.getServer;

public class ResourcePack {
    FileSystem fileSystem;
    private final Map<String, Namespace> namespaces = new HashMap<>();
    private final String name;

    public ResourcePack(String name){
        fileSystem = Jimfs.newFileSystem(Configuration.unix());
        this.name = name;
        createPath("/assets");

        Namespace minecraft = new Namespace("minecraft", this);

    }
    public String getName(){
        return name;

    }

    public void copyFileFromDisk(String filePath, String outputPath) {
        try {
            Path directory = fileSystem.getPath(outputPath);
            Path path = Paths.get(filePath);
            if (!Files.exists(directory)) {
                Files.createDirectories(directory);
            }

            Files.copy(path, directory, StandardCopyOption.REPLACE_EXISTING);

        } catch (IOException e) {
            throw new RuntimeException(e);

        }
    }

    @ApiStatus.Internal
    public void registerNamespace(Namespace namespace){ // registers texture packs so I can keep track on namespaces
        namespaces.put(namespace.getNamespaceAsString(), namespace);

    }

    public void createPath(String path){
        try {
            Path directory = fileSystem.getPath(path);
            Files.createDirectories(directory);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public void createGenericFile(String fileName, String path, String contents){
        try {
            Path directory = fileSystem.getPath(path);

            if (!Files.exists(directory)) {
                Files.createDirectories(directory);
            }



            Path file = directory.resolve(fileName);

            Files.write(file, ImmutableList.of(contents), StandardCharsets.UTF_8);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public void base64ToTexture(String fileName, String path, String textureAsBase64 ) {
        try {
            Path directory = fileSystem.getPath(path);

            byte[] decodedBytes = Base64.getDecoder().decode(textureAsBase64);


            if (!Files.exists(directory)) {
                Files.createDirectories(directory);
            }

            Path file = directory.resolve(fileName);

            Files.write(file, decodedBytes);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public boolean namespaceExists(String namespace){
        if (namespaces.containsKey(namespace))
            return true;
        else{
            return false;
        }

    }

    public Namespace getNamespace(String namespace){
        if (namespaces.containsKey(namespace))
            return namespaces.get(namespace);

        return null;

    }

    public boolean hasFile(String filePath){
        Path directory = fileSystem.getPath(filePath);
        return Files.exists(directory);
    }

    public void writeJsonObject(Writable object, String writePath){
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            createGenericFile(object.getFileName(), writePath, objectMapper.writeValueAsString(object));



        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }

    public void build(String outputPath) {
        try {
            ZipFileUtil zipFileUtil = new ZipFileUtil(outputPath, fileSystem);
            zipFileUtil.addToZip("");
            //zipFileTest.addToZip("pack.mcmeta");
            if (!hasFile("pack.mcmeta")){
                throw new RuntimeException("Resource pack dose not have pack.mcmeta!");
            };
            zipFileUtil.endZip();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }








}
