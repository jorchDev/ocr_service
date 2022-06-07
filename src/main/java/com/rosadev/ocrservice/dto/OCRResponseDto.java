package com.rosadev.ocrservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * OCRResponseDto
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OCRResponseDto {

    private Long idOCR;
    private String nombreDocumento;
    private String textoEncontrado;
    private String textoProcesado;
}