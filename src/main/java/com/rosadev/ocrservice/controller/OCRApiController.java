package com.rosadev.ocrservice.controller;

import java.io.File;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.rosadev.ocrservice.dto.OCRResponseDto;
import com.rosadev.ocrservice.lib.FileHandler;
import com.rosadev.ocrservice.service.OCRService;



@RestController
public class OCRApiController 
{    
   

    @Autowired
    FileHandler fileHandler;

    @Autowired
    OCRService ocrService;

    
    @RequestMapping(value = { "/api/ocr" }, produces = { "application/json;charset=UTF-8" }, method = { RequestMethod.POST })
    public OCRResponseDto ocrImage(final HttpServletRequest request, @RequestParam("file") MultipartFile file,
    @RequestParam(name="x",required = false ) Integer x,
    @RequestParam(name="y" ,required = false) Integer y,
    @RequestParam(name="width" ,required = false) Integer width,
    @RequestParam(name="height" ,required = false) Integer height,
    @RequestParam(name="bpp" ,required = false ) Integer bpp
    )
    {

        

        return ocrService.ocrImage(new File(fileHandler.saveMultipartFile(file, true)), null, null, null, null, null);
        
    }
   
   
}
