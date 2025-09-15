package org.rauka.dm.msapdfgenerator.service;

import org.rauka.dm.msapdfgenerator.dto.AccountDTO;
import org.rauka.dm.msapdfgenerator.service.models.PdfGenerationResponse;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

public interface PdfGeneratorService {

    Mono<PdfGenerationResponse> generatePdfReport(LocalDate fromDate, LocalDate toDate, String title);
}
