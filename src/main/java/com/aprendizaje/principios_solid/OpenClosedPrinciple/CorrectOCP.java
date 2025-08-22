package com.aprendizaje.principios_solid.OpenClosedPrinciple;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CorrectOCP {

    class IterGuideController{

        private final IterGuideService iterGuideService;

        public IterGuideController() {
            this.iterGuideService = new IterGuideServiceImpl(
                    new IterGuideRepositoryImpl(),
                    new IterGuideApiSunatImpl(),
                    new ReportServiceImpl(
                            List.of(
                                    new CorrectOCP().new PdfDispatchGuideReport(),
                                    new CorrectOCP().new PdfIterGuideReport())));
        }

        public List<?> getFirstPartIterGuide(){
            List<ReportGenerator> a = new ArrayList();
            return iterGuideService.getFirstPartIterGuide();
        }

        public List<?> findByIterGuideNumber(){
            return iterGuideService.findByIterGuideNumber();
        }

        public void registerIterGuide() throws DocumentException, FileNotFoundException {
            iterGuideService.registerIterGuide();
        }

        public void generateReportCopyIterGuide() throws DocumentException, FileNotFoundException {
            iterGuideService.generateReportPdf(PdfDispatchGuideReport.class);
        }
    }

    interface IterGuideService{

        List<?> getFirstPartIterGuide();
        List<?> findByIterGuideNumber();
        void registerIterGuide() throws DocumentException, FileNotFoundException;
        void generateReportPdf(Class<? extends ReportGenerator> clazz) throws DocumentException, FileNotFoundException;
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
            reportService.generate(PdfIterGuideReport.class, new HashMap<>(), "guide-number");
        }
        
        @Override
        public void generateReportPdf(Class<? extends ReportGenerator> clazz) throws DocumentException, FileNotFoundException {
            iterGuideRepository.findByIterGuideNumberResult();
            reportService.generate(clazz, new HashMap<>(), "guide-number");
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

        void generate(Class<? extends ReportGenerator> clazz, Map<String,?> data, String name) throws DocumentException, FileNotFoundException;
    }

    class ReportServiceImpl implements ReportService{

        private final Map<Class<? extends ReportGenerator>, ReportGenerator> generators = new HashMap<>();

        public ReportServiceImpl(List<ReportGenerator> listGenerator) {
            for (ReportGenerator generator : listGenerator) {
                generators.put(generator.getClass(), generator);
            }
        }

        @Override
        public void generate(Class<? extends ReportGenerator> clazz, Map<String, ?> data, String name) throws DocumentException, FileNotFoundException {
            ReportGenerator generator = generators.get(clazz);
            if (generator == null) {
                throw new IllegalArgumentException("Report type not supported: " + clazz);
            }
            generator.generateReport(data,name);
        }
    }

    interface ReportGenerator{

        void generateReport(Map<String, ?> data, String name) throws FileNotFoundException, DocumentException;
    }

    abstract class ItextReportGenerator implements ReportGenerator{

        @Override
        public void generateReport(Map<String, ?> data, String name) throws FileNotFoundException, DocumentException {
            Document document = new Document();
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(name));
            document.open();
            buildPdf(document, data);
            document.close();
        }

        protected abstract void buildPdf(Document document, Map<String, ?> data) throws DocumentException;
    }

    abstract class JasperReportGenerator implements ReportGenerator{

        @Override
        public void generateReport(Map<String, ?> data, String name){
            System.out.println("Generando con Jasper...");
            fillReport(data);
        }

        protected abstract void fillReport(Map<String,?> data);
    }

    class PdfIterGuideReport extends ItextReportGenerator{

        public PdfIterGuideReport() {
        }

        @Override
        protected void buildPdf(Document document, Map<String, ?> data) throws DocumentException {
            document.add(new Paragraph("PDF Iter guide"));
        }
    }

    class PdfDispatchGuideReport extends ItextReportGenerator{

        public PdfDispatchGuideReport() {
        }

        @Override
        protected void buildPdf(Document document, Map<String, ?> data) throws DocumentException {
            document.add(new Paragraph("PDF dispatch"));
        }
    }

}

/*
    En este ejemplo se cambió la lógica de verificar los tipos de reportes que el método tenía permitido generar
    mediante if/else a polimorfismo. Si bien se separó la responsabilidad de la generación de un reporte en un servicio
    aparte(ReportServiceImpl), colocar la lógica en un mismo método para cada tipo de reporte haría que se vaya
    modificando e incrementando en caso de que en un futuro el negocio necesite generar nuevos tipos de reportes, así
    que en este caso lo que se realizó fue que solo exista un solo método en la clase ReportServiceImpl, el cual se
    va a encargar de verificar que la clase que se reciba como parámetro sea una clase que implementa la interfaz
    ReportGenerator, aunque en este caso se separó en otra capa más, que sería extendiendo de una clase padre donde
    este implementa la interfaz ReportGenerator, el cual contiene un método donde se coloca código repetitivo, como la
    creación de el pdf dependiendo de la librería que se está utilizando, y se hizo con la finalidad de que en caso en
    un futuro se use una nueva librería para la generación de reportes, entonces se crea otra clase padre que
    implemente la interfaz ReportGenerator y esta sea extendida a otras clases con otro método para colocar el
    código personalizado para cada reporte, y en este ejemplo sería el "buildPdf" de la clase ItextReportGenerator.

    Entonces esta implementación de la generación de reportes cumple con el principio OCP, abierta a extensión y cerrada
    a modificación, ya que por cada nuevo tipo de reporte se necesite imprimir simplemente se necesita crear una nueva
    clase que EXTENDIDA de una clase padre que implemente la interfaz ReportGenerator, como podría ser la clase
    ItextReportGenerator o la clase JasperReportGenerator, y esta creación de una nueva clase no necesita MODIFICAR la
    clase ReportServiceImpl, cumpliendo con el principio.

    Otro punto a aclarar es que en este caso ya no se inyecta la interfaz ReportService en el controlador como en los
    ejemplos anteriores, debido a que en este ejemplo se asume que la generación de un reporte no es opcional, sino
    obligatorio, lo que iría dentro de la lógica de negocio, permitiendo que se necesite inyectar la interfaz
    ReportService dentro de la clase IterGuideServiceImpl, que a su vez cumple con el principio de SRP, ya que se
    delega la generación del reporte a un método de la implementación de la interfaz ReportService.
 */
