package org.rauka.dm.msapdfgenerator.service.impl;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Cell;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.rauka.dm.msapdfgenerator.dto.AccountDTO;
import org.rauka.dm.msapdfgenerator.dto.ClienteDTO;
import org.rauka.dm.msapdfgenerator.dto.MovementReportDTO;
import org.rauka.dm.msapdfgenerator.repository.MovementRepository;
import org.rauka.dm.msapdfgenerator.service.AccountService;
import org.rauka.dm.msapdfgenerator.service.ClientService;
import org.rauka.dm.msapdfgenerator.service.PdfGeneratorService;
import org.rauka.dm.msapdfgenerator.service.models.PdfGenerationResponse;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PdfGeneratorServiceImpl implements PdfGeneratorService {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final MovementRepository movementRepository;
    private final AccountService accountService;
    private final ClientService clientService;

    @Override
    public Mono<PdfGenerationResponse> generatePdfReport(LocalDate fromDate, LocalDate toDate, String title) {
        log.info("Generating simplified PDF report from {} to {}", fromDate, toDate);

        return movementRepository.findAllMovementReportByDate(fromDate, toDate)
            .collectList()
            .flatMap(movements -> {
                log.info("Found {} movement reports for the specified date range", movements.size());

                if (!movements.isEmpty()) {
                    Long firstAccountId = movements.getFirst().getAccountId();
                    log.debug("Starting enrichment flow for accountId: {}", firstAccountId);

                    return accountService.getAccountById(firstAccountId)
                        .flatMap(account -> {
                            log.debug("Account found: {} - identification: {}",
                                account, account.getIdentification());
                            return clientService.getClienteByIdentificacion(account.getIdentification())
                                .map(cliente -> generateSimplifiedReport(movements, fromDate, toDate, title, account, cliente))
                                .switchIfEmpty(Mono.error(new RuntimeException("Client not found for identification: " + account.getIdentification())));
                        })
                        .switchIfEmpty(Mono.error(new RuntimeException("Account not found for ID: " + firstAccountId)));
                } else {
                    return Mono.error(new RuntimeException("No movements found for the specified date range"));
                }
            });
    }

    private PdfGenerationResponse generateSimplifiedReport(List<MovementReportDTO> movements,
                                                         LocalDate fromDate,
                                                         LocalDate toDate,
                                                         String title,
                                                         AccountDTO account,
                                                         ClienteDTO cliente) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document document = new Document(pdfDoc);

            // Título del reporte
            String reportTitle = title != null && !title.trim().isEmpty() ? title : "Movement Report";
            document.add(new Paragraph(reportTitle).setBold().setFontSize(18));
            document.add(new Paragraph("Report Period: " +
                fromDate.format(DATE_FORMATTER) + " to " + toDate.format(DATE_FORMATTER)));
            document.add(new Paragraph("\n"));

            // Calcular total amount
            BigDecimal totalAmount = movements.stream()
                .map(MovementReportDTO::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

            // Mostrar resumen básico
            document.add(new Paragraph("Client: " + cliente.getFullName()));
            document.add(new Paragraph("Account Number: " + account.getAccountNumber()));
            document.add(new Paragraph("Total Amount: $" + totalAmount));
            document.add(new Paragraph("\n"));

            if (!movements.isEmpty()) {
                // Tabla simplificada con solo 6 columnas
                Table table = new Table(6);
                table.setWidth(500);

                // Encabezados
                table.addHeaderCell(new Cell().add(new Paragraph("Date").setBold()));
                table.addHeaderCell(new Cell().add(new Paragraph("Client").setBold()));
                table.addHeaderCell(new Cell().add(new Paragraph("Account Number").setBold()));
                table.addHeaderCell(new Cell().add(new Paragraph("Tipo").setBold()));
                table.addHeaderCell(new Cell().add(new Paragraph("Saldo").setBold()));
                table.addHeaderCell(new Cell().add(new Paragraph("Total Amount").setBold()));

                // Datos de los movimientos
                for (MovementReportDTO movement : movements) {
                    table.addCell(new Cell().add(new Paragraph(movement.getOccurredAt().format(DATE_FORMATTER))));
                    table.addCell(new Cell().add(new Paragraph(cliente.getFullName())));
                    table.addCell(new Cell().add(new Paragraph(account.getAccountNumber())));
                    table.addCell(new Cell().add(new Paragraph(movement.getMovementType())));
                    table.addCell(new Cell().add(new Paragraph("$" + movement.getBalance())));
                    table.addCell(new Cell().add(new Paragraph("$" + movement.getAmount())));
                }

                document.add(table);
            } else {
                document.add(new Paragraph("No movements found for the specified date range.").setItalic());
            }

            document.close();

            // Generar respuesta
            byte[] pdfBytes = baos.toByteArray();
            String base64Pdf = Base64.getEncoder().encodeToString(pdfBytes);

            String fileName = String.format("movement_report_%s_%s_to_%s.pdf",
                cliente.getFullName().replaceAll("\\s+", "_"),
                fromDate.format(DATE_FORMATTER),
                toDate.format(DATE_FORMATTER));

            log.info("Simplified PDF generated successfully. Client: {}, Account: {}, Movements: {}",
                cliente.getFullName(), account.getAccountNumber(), movements.size());

            PdfGenerationResponse response = new PdfGenerationResponse(base64Pdf, fileName, OffsetDateTime.now());
            response.setSize(pdfBytes.length);
            return response;

        } catch (Exception e) {
            log.error("Error generating simplified PDF report", e);
            throw new RuntimeException("Failed to generate PDF: " + e.getMessage(), e);
        }
    }
}
