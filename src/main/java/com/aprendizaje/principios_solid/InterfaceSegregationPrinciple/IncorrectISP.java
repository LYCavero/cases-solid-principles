package com.aprendizaje.principios_solid.InterfaceSegregationPrinciple;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IncorrectISP {

    class IterGuideController{

        private final IterGuideService iterGuideService;

        public IterGuideController() {
            this.iterGuideService = new IterGuideServiceImpl(
                    new IterGuideRepositoryImpl(),
                    new IterGuideApiSunatImpl(),
                    new ReportServiceImpl(
                            List.of()
                    ));
        }

        public List<?> getFirstPartIterGuide(){
            return iterGuideService.getFirstPartIterGuide();
        }

        public List<?> findByIterGuideNumber(){
            return iterGuideService.findByIterGuideNumber();
        }

        public void registerIterGuide() throws DocumentException, FileNotFoundException {
            iterGuideService.registerIterGuide(ReportType.PDF_ITER_GUIDE);
        }

        public void generateReportCopyIterGuide() throws DocumentException, FileNotFoundException {
            iterGuideService.generateReportCopyIterGuide(ReportType.PDF_DISPATCH_GUIDE);
        }

        public void generateExcelReportIterGuide() throws DocumentException, FileNotFoundException {
            iterGuideService.generateExcelReportIterGuide(ReportType.EXCEL_ITER_GUIDE_REPORT);
        }
    }

    interface IterGuideService{

        List<?> getFirstPartIterGuide();
        List<?> findByIterGuideNumber();
        void registerIterGuide(ReportType reportType) throws DocumentException, FileNotFoundException;
        void generateReportCopyIterGuide(ReportType reportType) throws DocumentException, FileNotFoundException;
        void generateExcelReportIterGuide(ReportType reportType) throws DocumentException, FileNotFoundException;
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
        public void registerIterGuide(ReportType reportType) throws DocumentException, FileNotFoundException {
            iterGuideRepository.saveIterGuideResultStepA();
            iterGuideApiSunat.sendIterGuideEmailResult();
            iterGuideRepository.saveIterGuideResultStepB();
            HashMap<String,String> map = new HashMap<>();
            reportService.generate(reportType,map,"iter-guide-number-report");
        }

        @Override
        public void generateReportCopyIterGuide(ReportType reportType) throws DocumentException, FileNotFoundException {
            iterGuideRepository.findByIterGuideNumberResult();
            HashMap<String,Integer> map = new HashMap<>();
            reportService.generate(reportType, map,"dispatch-guide-number-report");
        }

        @Override
        public void generateExcelReportIterGuide(ReportType reportType) throws DocumentException, FileNotFoundException {
            iterGuideRepository.findByIterGuideNumberResult();
            reportService.generate(reportType,List.of(),"iter-guide-number-excel-report");
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

        <T> void generate (ReportType reportType, T data, String name) throws FileNotFoundException, DocumentException;
    }

    class ReportServiceImpl implements ReportService{

        private final Map<ReportType, ReportGenerator<?>> generators = new EnumMap<>(ReportType.class);

        public ReportServiceImpl(List<ReportGenerator<?>> listGenerators) {
            for (ReportGenerator<?> gen: listGenerators){
                generators.put(gen.getType(),gen);
            }
        }

        @Override
        public <T> void generate(ReportType reportType, T data, String name) throws FileNotFoundException, DocumentException {
            ReportGenerator<T> generator = (ReportGenerator<T>) generators.get(reportType);
            if (generator == null) {
                throw new IllegalArgumentException("Tipo de reporte no soportado: " + reportType);
            }
            generator.generateReport(data,name);
        }
    }

    interface ReportGenerator<T>{

        void generateReport(T data, String name) throws FileNotFoundException, DocumentException;
        void editReport(T data, String filePath);
        ReportType getType();
    }

    abstract class PdfReportGenerator<T> implements ReportGenerator<T>{

        @Override
        public void generateReport(T data, String name) throws FileNotFoundException, DocumentException {
            Document document = new Document();
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(name));
            document.open();
            buildPdf(document, data);
            document.close();
        }

        @Override
        public void editReport(T data, String filePath) {
            throw new UnsupportedOperationException("This type of report cannot be edited.");
        }

        protected abstract void buildPdf(Document document, T data) throws DocumentException, FileNotFoundException;
    }

    abstract class ExcelReportGenerator<T> implements ReportGenerator<T>{

        @Override
        public void generateReport(T data, String name) {
            System.out.println("Generating an Excel file for editing...");
            documentExcel(data);
        }

        @Override
        public void editReport(T data, String filePath) {
            System.out.println("Find file in the specified path...");
            editExcel(data);
        }

        protected abstract void documentExcel(T data);

        protected abstract void editExcel(T data);
    }

    enum ReportType{
        PDF_ITER_GUIDE,
        PDF_DISPATCH_GUIDE,
        EXCEL_ITER_GUIDE_REPORT
    }

    class PdfIterGuideReport extends PdfReportGenerator<Map<String, String>> {

        @Override
        public void buildPdf(Document document, Map<String,String> data) throws FileNotFoundException, DocumentException {
            document.add(new Paragraph("PDF Iter guide"));
        }

        @Override
        public ReportType getType() {
            return ReportType.PDF_ITER_GUIDE;
        }
    }

    class PdfDispatchGuideReport extends PdfReportGenerator<Map<String, Integer>> {

        @Override
        public void buildPdf(Document document, Map<String, Integer> data) throws FileNotFoundException, DocumentException {
            document.add(new Paragraph("PDF dispatch"));
        }

        @Override
        public ReportType getType() {
            return ReportType.PDF_DISPATCH_GUIDE;
        }
    }

    class ExcelIterGuideReport extends ExcelReportGenerator<List<Integer>> {

        @Override
        public void documentExcel(List<Integer> data) {
            System.out.println("Document detail logic...");
        }

        @Override
        protected void editExcel(List<Integer> data) {
            System.out.println("Logic for edit the document...");
        }

        @Override
        public ReportType getType() {
            return ReportType.EXCEL_ITER_GUIDE_REPORT;
        }
    }

}
