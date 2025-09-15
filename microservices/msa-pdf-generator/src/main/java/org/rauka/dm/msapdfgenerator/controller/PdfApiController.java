package org.rauka.dm.msapdfgenerator.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.rauka.dm.msapdfgenerator.service.PdfGeneratorService;
import org.rauka.dm.msapdfgenerator.service.models.PdfGenerationResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Slf4j
@RestController
@RequiredArgsConstructor
public class PdfApiController implements PdfApi {

    private final PdfGeneratorService pdfGeneratorService;

    @Override
    public Mono<ResponseEntity<PdfGenerationResponse>> generatePdfReport(
            String fecha,
            String titulo,
            ServerWebExchange exchange) {

        log.info("Received PDF generation request - fecha: {}, titulo: {}", fecha, titulo);

        return Mono.fromCallable(() -> parseDateRange(fecha))
            .doOnNext(dates -> log.debug("Parsed dates - From: {}, To: {}", dates[0], dates[1]))
            .flatMap(dates -> {
                // Validate date range
                if (dates[0].isAfter(dates[1])) {
                    return Mono.error(new IllegalArgumentException("From date must be before to date"));
                }
                return pdfGeneratorService.generatePdfReport(dates[0], dates[1], titulo);
            })
            .map(ResponseEntity::ok)
            .doOnSuccess(response -> log.info("PDF generation completed successfully"))
            .doOnError(error -> log.error("Error generating PDF", error))
            .onErrorReturn(ResponseEntity.badRequest().build());
    }

    private LocalDate[] parseDateRange(String fecha) {
        try {
            String[] dateParts = fecha.split(",");
            if (dateParts.length != 2) {
                throw new IllegalArgumentException("Invalid date range format. Expected: YYYY-MM-DD,YYYY-MM-DD");
            }

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate fromDate = LocalDate.parse(dateParts[0].trim(), formatter);
            LocalDate toDate = LocalDate.parse(dateParts[1].trim(), formatter);

            return new LocalDate[]{fromDate, toDate};
        } catch (Exception e) {
            log.error("Error parsing date range: {}", fecha, e);
            throw new IllegalArgumentException("Invalid date format. Expected: YYYY-MM-DD,YYYY-MM-DD", e);
        }
    }
}
