package com.rosadev.ocrservice.lib;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;

import org.apache.commons.io.FilenameUtils;
import org.springframework.web.multipart.MultipartFile;



public class FileHandler {



	
    String filesPath ;
    
    /**
     * 
     * @param filesPath ruta base para el almacenamiento de archivos
     */
    public FileHandler(String filesPath)
    {
    	this.filesPath = filesPath;
    	
    }

	
    public enum fileType {
        GENERIC, DOCUMENT, IMAGEN, VIDEO, AUDIO, ZIP
    }

    
    public byte[] getFile(String fileName) {

        for (fileType fileType : fileType.values()) {

            String ruta = this.getPathFile(fileName, true, fileType);

            File file = new File(ruta);
            if (file.exists()) {
                byte[] fileBytes;
                try {
                    fileBytes = Files.readAllBytes(file.toPath());
                    return fileBytes;
                } catch (IOException e) {                    
                    e.printStackTrace();
                    return null;
                }
            }           
        }
        return null;        
        
    }

    public String getFileBase64(String fileName) {        
        
        String infoBase64 = "";
        String extension = FilenameUtils.getExtension(fileName).toLowerCase();
        if(extension.equals("png") || extension.equals("jpg") || extension.equals("jpeg"))        
        {            
            infoBase64 = "data:image/"+extension+";base64,";
        }       

        byte[] fileContent = this.getFile(fileName);
        String encodedString = Base64.getEncoder().encodeToString(fileContent);
        return infoBase64+encodedString;
    }

    /**
     * Guarda el archivo en una ruta determinada por el tipo de archivo
     * 
     * @param archivoBase64 cadena del archivo codificada en base 4
     * @param fileName especifica el nombre del archivo
     * @param conservarNombre si es verdadero el nombre de archivo será el especificado por el parametro fileName, en caso contrario, el nombre se generará automaticamente
     * @param tipoArchivo especifica el tipo de archivo
     * @return ruta absoluta donde se guardó el archivo
     */
    public String saveFileBase64(String archivoBase64, String fileName, boolean conservarNombre, fileType tipoArchivo) {

        byte[] decoder = Base64.getMimeDecoder().decode(archivoBase64);
        String ruta = this.getPathFile(fileName, conservarNombre);
        return saveByteArray(decoder, ruta);
    }

    /**
     * Guarda el archivo en una ruta determinada por el tipo de archivo de acuerdo a la extensión
     * 
     * @param archivoBase64 cadena del archivo codificada en base 4
     * @param fileName especifica el nombre del archivo
     * @param conservarNombre si es verdadero el nombre de archivo será el especificado por el parametro fileName, en caso contrario, el nombre se generará automaticamente
     * @return ruta absoluta donde se guardó el archivo
     */
    public String saveFileBase64(String archivoBase64, String fileName, boolean conservarNombre) {

        byte[] decoder = Base64.getMimeDecoder().decode(archivoBase64);
        String ruta = this.getPathFile(fileName, conservarNombre);
        return saveByteArray(decoder, ruta);
    }

    /**
     * Este metodo guarda el archivo especificado en base64 y devuelve la ruta
     * absoluta
     * 
     * @param archivoBase64 cadena del archivo codificado en base64
     * @return ruta absoluta donde se guardó el archivo
     */
    public String saveFileBase64(String archivoBase64) {

        if(archivoBase64.contains(";base64,") && archivoBase64.contains("data:"))
        {
            String infoString =  archivoBase64.split(";base64,")[0];
            infoString = infoString.replace("data:", "").replace(";base64,", "").replace("/", ".");            
            return this.saveFileBase64(archivoBase64.split(",")[1],  infoString, false);
        }

        return this.saveFileBase64(archivoBase64, "", false);
    }


      /**
     * Este metodo guarda el archivo especificado en formato Multipart y devuelve la
     * ruta absoluta
     * 
     * @param multipartFile archivo multipart
     * @param keepOriginalFileName especifica si debe conservase el nombre original del archivo
     * @return ruta absoluta donde se guardó el archivo
     */
    public String saveMultipartFile(MultipartFile multipartFile, boolean keepOriginalFileName) {

        byte[] bytes;
        try {
            bytes = multipartFile.getBytes();
            String ruta = this.getPathFile(multipartFile.getOriginalFilename(), keepOriginalFileName);
            return this.saveByteArray(bytes, ruta);

        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Este metodo guarda el archivo especificado en formato Multipart y devuelve la
     * ruta absoluta, el nombre del archivo cambiará por uno generado automaticamente 
     * 
     * @param multipartFile
     * @return ruta absoluta donde se guardó el archivo
     */
    public String saveMultipartFile(MultipartFile multipartFile) {

        return this.saveMultipartFile(multipartFile, false);
    }

    /**
     * Este metodo guarda el archivo especificado en en el arreglo de Byte y
     * devuelve la ruta absoluta
     * 
     * @param byteArray
     * @return ruta absoluta donde se guardó el archivo
     */
    public String saveByteArray(byte byteArray[]) {

        String ruta = this.getPathFile();
        return saveByteArray(byteArray, ruta);

    }

    /**
     * Este metodo guarda el archivo del byte array en la ruta especificada y
     * devuelve la ruta absoluta
     * 
     * @param byteArray
     * @return ruta absoluta donde se guardó el archivo
     */
    public String saveByteArray(byte byteArray[], String ruta) {

        try (FileOutputStream fileOutputStream = new FileOutputStream(ruta)) {
            fileOutputStream.write(byteArray);
            fileOutputStream.close();            
            return new File(ruta).getAbsolutePath();
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
  
    private fileType getTipoArchivo( String extension)
    {       
        
        switch (extension) {
            case "bpm": 
            case "gif": 
            case "png": 
            case "jpg":     return fileType.IMAGEN;

            case "doc":
            case "xls": 
            case "ppt": 
            case "pdf": 
            case "docx": 
            case "xlsx": 
            case "pptx": 
            case "txt":     return fileType.DOCUMENT; 

            case "avi":
            case "flv": 
            case "mp4": 
            case "mov": 
            case "mpeg":    return fileType.VIDEO;

            case "mp3":
            case "ogg": 
            case "wav":    return fileType.AUDIO;

            case "rar":
            case "7z": 
            case "zip":    return fileType.ZIP; 
            
            default: return fileType.GENERIC;                
        }
    }

    private String getPathFile() {
        return this.getPathFile("", false);
    }


    private String getPathFile(String fileName, boolean keepOriginalFileName) {

        String extension = FilenameUtils.getExtension(fileName).toLowerCase();        
        return  getPathFile( fileName,  keepOriginalFileName, this.getTipoArchivo(extension));

    }


    private String getPathFile(String fileName, boolean keepOriginalFileName, fileType tipoArchivo) {

        String extension = FilenameUtils.getExtension(fileName);

        String filePath = filesPath;
        if (!filePath.endsWith("/")) {
            filePath += "/";
        }
        filePath+= tipoArchivo.name().toLowerCase()+"/";
        File dir = new File(filePath);
        if(!dir.exists())// creamos directorio si no existe
        {
            dir.mkdir();
        }
        if (keepOriginalFileName) {
            return filePath + fileName;
        }

        String nombre = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        UUID uuid = UUID.randomUUID();        
        nombre+="_"+uuid.toString().substring(uuid.toString().length()-8);
        filePath = filePath + nombre + (extension.length() > 0 ? "." + extension : "");
        return filePath;
    }

}
