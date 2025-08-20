package com.aprendizaje.principios_solid.SingleResponsibilityPrinciple;

import java.util.List;

public class IncorrectSRP {

    class IterGuideController {
        IterGuideRepository iterGuideRepository = new IterGuideRepositoryImpl();
        IterGuideApiSunat iterGuideApiSunat = new IterGuideApiSunatImpl();
        IterGuideService iterGuideService = new IterGuideServiceImpl(iterGuideRepository, iterGuideApiSunat);

        public List<?> getFirstPartIterGuide() {
            return iterGuideService.getFirstPartIterGuide();
        }

        public List<?> findByIterGuideNumber() {
            return iterGuideService.findByIterGuideNumber();
        }

        public void registerIterGuide() {
            iterGuideService.registerIterGuide();
        }
    }

    interface IterGuideService{

        List<?> getFirstPartIterGuide();
        List<?> findByIterGuideNumber();
        void registerIterGuide();
    }

    class IterGuideServiceImpl implements IterGuideService{

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
            IterGuideReportPdfGenerator.generateIterGuideReportPdf();
        }

    }

    interface IterGuideRepository{

        int saveIterGuideResultStepA();
        int saveIterGuideResultStepB();
        List<?> findByIdResult();
        List<?> findByIterGuideNumberResult();
    }

    class IterGuideRepositoryImpl implements IterGuideRepository{


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

    class IterGuideApiSunatImpl implements IterGuideApiSunat{

        @Override
        public void sendIterGuideEmailResult() {
        }
    }

    static class IterGuideReportPdfGenerator {

        public static void generateIterGuideReportPdf(){
        }
    }


}

/*
    En este ejemplo se visualiza una violación del principio SRP en el metodo registerIterGuide() de la clase
    IterGuideServiceImpl, el cual en este ejemplo el flujo del registro de una guía sería:

    1. Se guarda una información básica de la guía en la base de datos, con un estado de 'EN PROCESO'
    2. Se consulta a una api externa para el registro de la guía, en este caso la SUNAT
    3. Se actualiza el estado dependiendo de la respuesta de la api, 'EMITIDO' o 'RECHAZADO'
    4. Generar un reporte de la guía en PDF

    el cual, desde el paso 1 hasta el 3 cumple con la responsabilidad de 'registro de la guía' que sería el proceso de
    negocio, pero el paso 4, que es la de generar un reporte, no sería necesaria para cumplir esta responsabilidad
 */


