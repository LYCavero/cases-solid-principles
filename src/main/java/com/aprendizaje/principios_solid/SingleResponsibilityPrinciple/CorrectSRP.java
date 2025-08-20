package com.aprendizaje.principios_solid.SingleResponsibilityPrinciple;

import java.util.List;

public class CorrectSRP {

    class IterGuideController{

        private final IterGuideRepository iterGuideRepository;
        private final IterGuideApiSunat iterGuideApiSunat;
        private final IterGuideService iterGuideService;
        private final ReportService reportService;

        public IterGuideController() {
            this.iterGuideRepository = new IterGuideRepositoryImpl();
            this.iterGuideApiSunat = new IterGuideApiSunatImpl();
            this.iterGuideService = new IterGuideServiceImpl(iterGuideRepository,iterGuideApiSunat);
            this.reportService = new ReportServiceImpl();
        }

        public List<?> getFirstPartIterGuide(){
            return iterGuideService.getFirstPartIterGuide();
        }

        public List<?> findByIterGuideNumber(){
            return iterGuideService.findByIterGuideNumber();
        }

        public void registerIterGuide(){
            iterGuideService.registerIterGuide();
            reportService.iterGuideGenerateReportPdf();
        }
    }

    interface IterGuideService{

        List<?> getFirstPartIterGuide();
        List<?> findByIterGuideNumber();
        void registerIterGuide();
    }

    class IterGuideServiceImpl implements IterGuideService {

        final IterGuideRepository iterGuideRepository;
        final IterGuideApiSunat iterGuideApiSunat;

        IterGuideServiceImpl(IterGuideRepository iterGuideRepository, IterGuideApiSunat iterGuideApiSunat) {
            this.iterGuideRepository = iterGuideRepository;
            this.iterGuideApiSunat = iterGuideApiSunat;
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
        public void registerIterGuide() {
            iterGuideRepository.saveIterGuideResultStepA();
            iterGuideApiSunat.sendIterGuideEmailResult();
            iterGuideRepository.saveIterGuideResultStepB();
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
        void iterGuideGenerateReportPdf();
    }

    class ReportServiceImpl implements ReportService{

        @Override
        public void iterGuideGenerateReportPdf() {
        }
    }
}

/*
    En este ejemplo se separó la responsabilidad de generar la guía y generar el reporte de la guía en servicios
    separados, cumpliendo con el mismo objetivo, que es la que generar el reporte luego de que se haya emitido una guía,
    pero la diferencia está en que si luego, la lógica de la generación del pdf cambia, no afecta a la lógica de la
    generación de la guía que sí funciona, y de la misma manera, si la api externa modifica la manera en como se

 */

