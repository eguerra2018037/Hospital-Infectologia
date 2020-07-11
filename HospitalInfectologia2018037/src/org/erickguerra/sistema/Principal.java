package org.erickguerra.sistema;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.erickguerra.controller.AreaController;
import org.erickguerra.controller.CargoController;
import org.erickguerra.controller.ContactoUrgenciaController;
import org.erickguerra.controller.ControlCitaController;
import org.erickguerra.controller.EspecialidadController;
import org.erickguerra.controller.HorarioController;
import org.erickguerra.controller.MedicoController;
import org.erickguerra.controller.MedicoEspecialidadController;
import org.erickguerra.controller.MenuPrincipalController;
import org.erickguerra.controller.PacienteController;
import org.erickguerra.controller.ProgramadorController;
import org.erickguerra.controller.RecetaController;
import org.erickguerra.controller.ResponsableTurnoController;
import org.erickguerra.controller.TelefonoMedicoController;
import org.erickguerra.controller.TurnoController;
import org.erickguerra.report.GenerarReporte;

public class Principal extends Application {
    private final String PAQUETE_VISTA = "/org/erickguerra/view/";
    private Stage escenarioPrincipal;
    private Scene escena;
    
    @Override
    public void start(Stage escenarioPrincipal){
        this.escenarioPrincipal = escenarioPrincipal;
        escenarioPrincipal.setTitle("Hospital de Infectología");
        menuPrincipal();
        escenarioPrincipal.show();
        escenarioPrincipal.getIcons().add(new Image("/org/erickguerra/images/descarga.png"));
        escenarioPrincipal.setResizable(false);
 
    }
    
    public void menuPrincipal(){
        try{
            MenuPrincipalController menuPrincipal = (MenuPrincipalController)cambiarEscena("MenuPrincipalView.fxml", 560, 456);
            menuPrincipal.setEscenarioPrincipal(this);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void ventanaMedicos(){
        try{
            MedicoController medicoController = (MedicoController)cambiarEscena("MedicoView.fxml", 813, 600);
            medicoController.setEscenarioPrincipal(this);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void ventanaPacientes(){
        try{
            PacienteController pacienteController = (PacienteController)cambiarEscena("PacienteView.fxml", 940,526);
            pacienteController.setEscenarioPrincipal(this);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void ventanaResponsableTurno(){
        try{
            ResponsableTurnoController responsableTurnoController = (ResponsableTurnoController)cambiarEscena("ResponsableTurnoView.fxml", 720, 457);
            responsableTurnoController.setEscenarioPrincipal(this);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void ventanaEspecialidades(){
        try{
            EspecialidadController especialidadController = (EspecialidadController)cambiarEscena("EspecialidadView.fxml", 462, 293);
            especialidadController.setEscenarioPrincipal(this);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void ventanaContactoUrgencia(){
        try{
            ContactoUrgenciaController contactoUrgenciaController = (ContactoUrgenciaController)cambiarEscena("ContactoUrgenciaView.fxml", 679, 466);
            contactoUrgenciaController.setEscenarioPrincipal(this);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void ventanaAreas(){
        try{
            AreaController areaController = (AreaController)cambiarEscena("AreaView.fxml", 462, 293);
            areaController.setEscenarioPrincipal(this);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void ventanaCargos(){
        try{
            CargoController cargoController = (CargoController)cambiarEscena("CargoView.fxml", 462, 293);
            cargoController.setEscenarioPrincipal(this);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void ventanaTelefonosMedicos(){
        try{
            TelefonoMedicoController telefonoMedicoController = (TelefonoMedicoController)cambiarEscena("TelefonoMedicoView.fxml", 600, 400);
            telefonoMedicoController.setEscenarioPrincipal(this);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void ventanaMedicoEspecialidad(){
        try{
            MedicoEspecialidadController medicoEspecialidadController = (MedicoEspecialidadController)cambiarEscena("MedicoEspecialidadView.fxml", 640, 466);
            medicoEspecialidadController.setEscenarioPrincipal(this);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void ventanaProgramador(){
        try{
            ProgramadorController programadorController = (ProgramadorController)cambiarEscena("ProgramadorView.fxml", 600, 400);
            programadorController.setEscenarioPrincipal(this);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
     
    public void ventanaHorarios(){
        try{
            HorarioController horarioController = (HorarioController)cambiarEscena("HorarioView.fxml", 462, 439);
            horarioController.setEscenarioPrincipal(this);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void ventanaRecetas(){
        try{
            RecetaController recetaController = (RecetaController)cambiarEscena("RecetaView.fxml", 433,418);
            recetaController.setEscenarioPrincipal(this);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void ventanaControlCitas(){
        try{
            ControlCitaController controlCitaController = (ControlCitaController) cambiarEscena("ControlCitaView.fxml", 609, 441);
            controlCitaController.setEscenarioPrincipal(this);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void ventanaTurno(){
        try{
            TurnoController turnoController = (TurnoController)cambiarEscena("TurnoView.fxml", 784, 443);
            turnoController.setEscenarioPrincipal(this);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void reporteMedicos(){
        try{
            Map parametros = new HashMap();
            parametros.put("codigoMedico", null);
            GenerarReporte.mostrarReporte("ReporteMedicos.jasper", "Reporte de Médicos", parametros);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void reportePacientes(){
        try{
            Map parametros = new HashMap();
            parametros.put("codigoPaciente", null);
            GenerarReporte.mostrarReporte("ReportePacientes.jasper", "Reporte de Pacientes", parametros);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void reporteControlCitas(){
        try{
            ControlCitaController controlCitaController = new ControlCitaController();
            controlCitaController.imprimirReporte();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public Initializable cambiarEscena(String fxml, int ancho, int alto) throws Exception{
        Initializable resultado = null;
        FXMLLoader cargadorFXML = new FXMLLoader();
        InputStream archivo = Principal.class.getResourceAsStream(PAQUETE_VISTA + fxml);
        cargadorFXML.setBuilderFactory(new JavaFXBuilderFactory());
        cargadorFXML.setLocation(Principal.class.getResource(PAQUETE_VISTA + fxml));
        escena = new Scene((AnchorPane)cargadorFXML.load(archivo),ancho,alto);
        escenarioPrincipal.setScene(escena);
        escenarioPrincipal.sizeToScene();
        resultado = (Initializable)cargadorFXML.getController();
                
        return resultado;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
