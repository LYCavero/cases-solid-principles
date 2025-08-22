package com.aprendizaje.principios_solid.OpenClosedPrinciple;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.List;

public class IncorrectOCP {

    class IterGuideController{

        private final IterGuideService iterGuideService;

        public IterGuideController() {
            this.iterGuideService = new IterGuideServiceImpl(
                    new IterGuideRepositoryImpl(),
                    new IterGuideApiSunatImpl(),
                    new ReportServiceImpl());
        }

        public List<?> getFirstPartIterGuide(){
            return iterGuideService.getFirstPartIterGuide();
        }

        public List<?> findByIterGuideNumber(){
            return iterGuideService.findByIterGuideNumber();
        }

        public void registerIterGuide() throws DocumentException, FileNotFoundException {
            iterGuideService.registerIterGuide();
        }

        public void generateReportCopyIterGuide(String type) throws DocumentException, FileNotFoundException {
            iterGuideService.generateReportPdf(type);
        }
    }

    interface IterGuideService{

        List<?> getFirstPartIterGuide();
        List<?> findByIterGuideNumber();
        void registerIterGuide() throws DocumentException, FileNotFoundException;
        void generateReportPdf(String type) throws DocumentException, FileNotFoundException;
    }

    class IterGuideServiceImpl implements IterGuideService {

        final IterGuideRepository iterGuideRepository;
        final IterGuideApiSunat iterGuideApiSunat;
        final ReportService reportService;

        IterGuideServiceImpl(IterGuideRepository iterGuideRepository, IterGuideApiSunat iterGuideApiSunat, ReportService reportService) {
            this.iterGuideRepository = iterGuideRepository;
            this.iterGuideApiSunat = iterGuideApiSunat;
            this.reportService = reportService;
        }

        @Override
        public List<?> getFirstPartIterGuide() {
            List<?> list = iterGuideRepository.findByIdResult();
            return list;
        }

        @Override
        public List<?> findByIterGuideNumber() {
            List<?> list = iterGuideRepository.findByIterGuideNumberResult();
            return list;
        }

        @Override
        public void registerIterGuide() throws DocumentException, FileNotFoundException {
            iterGuideRepository.saveIterGuideResultStepA();
            iterGuideApiSunat.sendIterGuideEmailResult();
            iterGuideRepository.saveIterGuideResultStepB();
            reportService.generateReportPdf("iter");
        }

        @Override
        public void generateReportPdf(String type) throws DocumentException, FileNotFoundException {
            iterGuideRepository.findByIterGuideNumberResult();
            reportService.generateReportPdf("iter");
        }

    }

    interface IterGuideRepository{

        int saveIterGuideResultStepA();
        int saveIterGuideResultStepB();
        List<?> findByIdResult();
        List<?> findByIterGuideNumberResult();
    }

    class IterGuideRepositoryImpl implements IterGuideRepository {


        @Override
        public int saveIterGuideResultStepA() {
            return 0;
        }

        @Override
        public int saveIterGuideResultStepB() {
            return 0;
        }

        @Override
        public List<?> findByIdResult() {
            return List.of();
        }

        @Override
        public List<?> findByIterGuideNumberResult() {
            return List.of();
        }
    }

    interface IterGuideApiSunat{

        void sendIterGuideEmailResult();
    }

    class IterGuideApiSunatImpl implements IterGuideApiSunat {

        @Override
        public void sendIterGuideEmailResult() {
        }
    }

    interface ReportService{
        void generateReportPdf(String type) throws FileNotFoundException, DocumentException;
    }

    class ReportServiceImpl implements ReportService{

        @Override
        public void generateReportPdf(String type) throws FileNotFoundException, DocumentException {
            Document document = new Document();
            if(type.equals("iter")){    //violación del OCP
                PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream("report-iter.pdf"));
                document.open();
                document.add(new Paragraph("PDF Iter guide"));
            } else if (type.equals("dispatch")) {   //violación del OCP
                PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream("report-dispatch.pdf"));
                document.open();
                document.add(new Paragraph("PDF dispatch"));
            } else {
                System.out.println("Incorrect report type");
            }
            document.close();
        }
    }

}

/*
    En este ejemplo se visualiza una violación al principio de abierto/cerrado (OCP) en el método
    iterGuideGenerateReportPdf de la clase ReportServiceImpl, el cual en este ejemplo el flujo para generar un reporte
    es el siguiente:

    1. Se envía como parámetro el tipo de reporte que se quiere imprimir
    2. El método verifica si el tipo de reporte que se quiere imprimir existe
    3. Imprime el tipo de reporte

    en el cual, si bien el flujo para generar un reporte funcionaría, no cumple con el principio de abierto cerrado, ya
    que, si más adelante existen nuevos tipos de reportes que se necesitan imprimir como por ejemplo "inventory", se
    requerirá ingresar nuevamente a esta clase y a este método agregando una nueva verificación, y el principio de OCP
    indica que debe estar abierta a extensión y cerrada a modificación, y en este caso estaría abierta a modificación.

    El principio OCP no te impide modificar una clase para agregar funcionalidades, como por ejemplo el agregar un
    nuevo método en la clase ReportServiceImpl como en este ejemplo el cual se llama generateReportPdf, ya que se está
    agregando una funcionalidad en una clase diseñada para almacenar la lógica de negocio, las cuales en esta
    arquitectura serían las clases ServiceImpl, sino que impide el agregar if/else o switch para manejar "tipos" o
    "variación" de un mismo comportamiento dentro de un mismo método, como en los casos de reportes.
*/