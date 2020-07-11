package org.erickguerra.controller;

import eu.schudt.javafx.controls.calendar.DatePicker;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javax.swing.JOptionPane;
import org.erickguerra.bean.MedicoEspecialidad;
import org.erickguerra.bean.Paciente;
import org.erickguerra.bean.ResponsableTurno;
import org.erickguerra.bean.Turno;
import org.erickguerra.db.Conexion;
import org.erickguerra.sistema.Principal;

public class TurnoController implements Initializable{
    private Principal escenarioPrincipal;
    private enum operaciones{NUEVO, NINGUNO, CANCELAR, ELIMINAR, GUARDAR,ACTUALIZAR,EDITAR}
    private operaciones tipoDeOperacion = operaciones.NINGUNO;
    private ObservableList<Turno> listaTurno;
    private ObservableList<MedicoEspecialidad> listaMedicoEspecialidad;
    private ObservableList<ResponsableTurno> listaResponsablesTurno;
    private ObservableList<Paciente> listaPaciente;
    private DatePicker fechaTurno;
    private DatePicker fechaCita;
    
    @FXML TextField txtValorCita;
    @FXML ComboBox cmbCodigoMedicoEspecialidad;
    @FXML ComboBox cmbCodigoResponsableTurno;
    @FXML ComboBox cmbCodigoPaciente;
    @FXML GridPane grpTurno;
    @FXML GridPane grpCita;
    @FXML TableView tblTurnos;
    @FXML TableColumn colCodigoTurno;
    @FXML TableColumn colTurno;
    @FXML TableColumn colCita;
    @FXML TableColumn colValorCita;
    @FXML TableColumn colCodigoMedicoEspecialidad;
    @FXML TableColumn colCodigoResponsableTurno;
    @FXML TableColumn colCodigoPaciente;
    @FXML Button btnNuevo;
    @FXML Button btnEliminar;
    @FXML Button btnEditar;
    @FXML Button btnReporte;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cargarDatos();
        fechaTurno = new DatePicker(Locale.ENGLISH);
        fechaTurno.setDateFormat(new SimpleDateFormat("yyy-MM-dd"));
        fechaTurno.getCalendarView().todayButtonTextProperty().set("Today");
        fechaTurno.getCalendarView().setShowWeeks(false);
        fechaTurno.getStylesheets().add("/org/erickguerra/resource/DatePicker.css");
        grpTurno.add(fechaTurno,0,0);
        fechaTurno.setDisable(true);
        fechaCita = new DatePicker(Locale.ENGLISH);
        fechaCita.setDateFormat(new SimpleDateFormat("yyy-MM-dd"));
        fechaCita.getCalendarView().todayButtonTextProperty().set("Today");
        fechaCita.getCalendarView().setShowWeeks(false);
        fechaCita.getStylesheets().add("/org/erickguerra/resource/DatePicker.css");
        grpCita.add(fechaCita,0,0);
        fechaCita.setDisable(true);
        cmbCodigoMedicoEspecialidad.setItems(getMedicosEspecialidad());
        cmbCodigoResponsableTurno.setItems(getResponsablesTurno());
        cmbCodigoPaciente.setItems(getPacientes());
    }
    
    public void cargarDatos(){
        tblTurnos.setItems(getTurnos());
        colCodigoTurno.setCellValueFactory(new PropertyValueFactory<Turno, Integer>("codigoTurno"));
        colTurno.setCellValueFactory(new PropertyValueFactory<Turno, Date>("fechaTurno"));
        colCita.setCellValueFactory(new PropertyValueFactory<Turno, Date>("fechaCita"));
        colValorCita.setCellValueFactory(new PropertyValueFactory<Turno, Double>("valorCita"));
        colCodigoMedicoEspecialidad.setCellValueFactory(new PropertyValueFactory<Turno, Integer>("codigoMedicoEspecialidad"));
        colCodigoResponsableTurno.setCellValueFactory(new PropertyValueFactory<Turno, Integer>("codigoResponsableTurno"));
        colCodigoPaciente.setCellValueFactory(new PropertyValueFactory<Turno, Integer>("codigoPaciente"));
    }
    
    public ObservableList<Turno> getTurnos(){
        ArrayList<Turno> lista = new ArrayList<Turno>();
        try{
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_ListarTurnos()}");
            ResultSet resultado = procedimiento.executeQuery();
            while(resultado.next()){
                lista.add(new Turno(resultado.getInt("codigoTurno"),
                    resultado.getDate("fechaTurno"),
                    resultado.getDate("fechaCita"),
                    resultado.getDouble("valorCita"),
                    resultado.getInt("codigoMedicoEspecialidad"),
                    resultado.getInt("codigoResponsableTurno"),
                    resultado.getInt("codigoPaciente")));
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return listaTurno = FXCollections.observableList(lista);
    }
    
    public Turno buscarTurno(int codigoTurno){
        Turno resultado = null;
        try{
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_BuscarTurno(?)}");
            procedimiento.setInt(1, codigoTurno);
            ResultSet registro = procedimiento.executeQuery();
            while(registro.next()){
                resultado = new Turno(registro.getInt("codigoTurno"),
                    registro.getDate("fechaTurno"),
                    registro.getDate("fechaCita"),
                    registro.getDouble("valorCita"),
                    registro.getInt("codigoMedicoEspecialidad"),
                    registro.getInt("codigoResponsableTurno"),
                    registro.getInt("codigoPaciente"));
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return resultado;
    }
    
    public MedicoEspecialidad buscarMedicoEspecialidad(int codigoMedicoEspecialidad){
        MedicoEspecialidad resultado = null;
        try{
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_BuscarMedicoEspecialidad(?)}");
            procedimiento.setInt(1, codigoMedicoEspecialidad);
            ResultSet registro = procedimiento.executeQuery();
            while(registro.next()){
                resultado = new MedicoEspecialidad(registro.getInt("codigoMedicoEspecialidad"),
                    registro.getInt("codigoMedico"),
                    registro.getInt("codigoEspecialidad"),
                    registro.getInt("codigoHorario"));
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return resultado;
    }
    
    public ResponsableTurno buscarResponsableTurno(int codigoResponsableTurno){
        ResponsableTurno resultado = null;
        try{
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_BuscarReponsableTurno(?)}");
            procedimiento.setInt(1, codigoResponsableTurno);
            ResultSet registro = procedimiento.executeQuery();
            while(registro.next()){
                resultado = new ResponsableTurno(registro.getInt("codigoResponsableTurno"),
                    registro.getString("nombreResponsable"),
                    registro.getString("apellidosResponsable"),
                    registro.getString("telefonoPersonal"),
                    registro.getInt("codigoArea"),
                    registro.getInt("codigoCargo"));
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return resultado;
    }
    
    public Paciente buscarPaciente(int codigoPaciente){
        Paciente resultado = null;
        try{
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_BuscarPaciente(?)}");
            procedimiento.setInt(1, codigoPaciente);
            ResultSet registro = procedimiento.executeQuery();
            while(registro.next()){
                resultado = new Paciente(registro.getInt("codigoPaciente"),
                    registro.getString("DPI"),
                    registro.getString("apellidos"),
                    registro.getString("nombres"),
                    registro.getDate("fechaNacimiento"),
                    registro.getInt("edad"),
                    registro.getString("direccion"),
                    registro.getString("ocupacion"),
                    registro.getString("sexo"));
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return resultado;
    }
    
    public ObservableList<MedicoEspecialidad> getMedicosEspecialidad(){
        ArrayList<MedicoEspecialidad> lista = new ArrayList<MedicoEspecialidad>();
        try{
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_ListarMedicoEspecialidad()}");
            ResultSet resultado = procedimiento.executeQuery();
            while(resultado.next()){
                lista.add(new MedicoEspecialidad(resultado.getInt("codigoMedicoEspecialidad"),
                    resultado.getInt("codigoMedico"),
                    resultado.getInt("codigoEspecialidad"),
                    resultado.getInt("codigoHorario")));
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return listaMedicoEspecialidad = FXCollections.observableList(lista);
    }
    
    public ObservableList<ResponsableTurno> getResponsablesTurno(){
        ArrayList<ResponsableTurno> lista = new ArrayList<ResponsableTurno>();
        try{
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_ListarResponsablesTurno()}");
            ResultSet resultado = procedimiento.executeQuery();
            while(resultado.next()){
                lista.add(new ResponsableTurno(resultado.getInt("codigoResponsableTurno"),
                    resultado.getString("nombreResponsable"),
                    resultado.getString("apellidosResponsable"),
                    resultado.getString("telefonoPersonal"),
                    resultado.getInt("codigoArea"),
                    resultado.getInt("codigoCargo")));
            }
            
        }catch(Exception e){
            e.printStackTrace();
        }
        return listaResponsablesTurno = FXCollections.observableList(lista);
    }
    
    public ObservableList<Paciente> getPacientes(){
        ArrayList<Paciente> lista = new ArrayList<Paciente>();
            try{
                PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_ListarPacientes()}");
                ResultSet resultado = procedimiento.executeQuery();
                    while(resultado.next()){
                        lista.add(new Paciente(resultado.getInt("codigoPaciente"),
                        resultado.getString("DPI"),
                        resultado.getString("Apellidos"),
                        resultado.getString("Nombres"),
                        resultado.getDate("FechaNacimiento"),
                        resultado.getInt("edad"),
                        resultado.getString("Direccion"),
                        resultado.getString("Ocupacion"),
                        resultado.getString("Sexo")));
                    }
            }catch(Exception e){
                e.printStackTrace();
            }
            return listaPaciente = FXCollections.observableList(lista);
    }
    
    public void seleccionarElemento(){
        if(tblTurnos.getSelectionModel().isEmpty()){
            tblTurnos.getSelectionModel();
        }else{
            fechaTurno.selectedDateProperty().set(((Turno)tblTurnos.getSelectionModel().getSelectedItem()).getFechaTurno());
            fechaCita.selectedDateProperty().set(((Turno)tblTurnos.getSelectionModel().getSelectedItem()).getFechaCita());
            txtValorCita.setText(String.valueOf(((Turno)tblTurnos.getSelectionModel().getSelectedItem()).getValorCita()));
            cmbCodigoMedicoEspecialidad.getSelectionModel().select(buscarTurno(((Turno)tblTurnos.getSelectionModel().getSelectedItem()).getCodigoMedicoEspecialidad()));
            cmbCodigoResponsableTurno.getSelectionModel().select(buscarTurno(((Turno)tblTurnos.getSelectionModel().getSelectedItem()).getCodigoResponsableTurno()));
            cmbCodigoPaciente.getSelectionModel().select(buscarTurno(((Turno)tblTurnos.getSelectionModel().getSelectedItem()).getCodigoPaciente()));
        }
    }
    
    public void eliminar(){
        switch(tipoDeOperacion){
            case GUARDAR:
                desactivarControles();
                tblTurnos.setDisable(false);
                btnNuevo.setText("Nuevo");
                btnEliminar.setText("Eliminar");
                btnEditar.setDisable(false);
                btnReporte.setDisable(false);
                tipoDeOperacion = operaciones.NINGUNO;
                limpiarControles();
                if(tblTurnos.getSelectionModel().getSelectedItem() != null){
                    seleccionarElemento();
                }
                break;
            default:
                seleccionarElemento();
                if(tblTurnos.getSelectionModel().getSelectedItem() != null){
                    int respuesta = JOptionPane.showConfirmDialog(null, "¿Está seguro de que desea eliminar el registro?", "Eliminar Turno", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                    if(respuesta == JOptionPane.YES_OPTION){
                        try{
                            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_EliminarTurno(?)}");
                            procedimiento.setInt(1, ((Turno) tblTurnos.getSelectionModel().getSelectedItem()).getCodigoTurno());
                            procedimiento.execute();
                            listaTurno.remove(tblTurnos.getSelectionModel().getSelectedIndex());
                            limpiarControles();
                        }catch(Exception e){
                            e.printStackTrace();
                        }
                    }
                }else{
                    JOptionPane.showMessageDialog(null, "Debe seleccionar un elemento");
                }
        }
    }
    
    public void nuevo(){
        switch(tipoDeOperacion){
            case NINGUNO:
                activarControles();
                limpiarControles();
                tblTurnos.setDisable(true);
                btnNuevo.setText("Guardar");
                btnEliminar.setText("Cancelar");
                btnEditar.setDisable(true);
                btnReporte.setDisable(true);
                tipoDeOperacion = operaciones.GUARDAR;
                break;
            case GUARDAR:
                boolean validarValorCita = validacion(txtValorCita.getText());
                if(fechaTurno.selectedDateProperty().get() == null||fechaCita.selectedDateProperty().get()==null|| txtValorCita.getText().equals("") || 
                        cmbCodigoMedicoEspecialidad.getSelectionModel().isEmpty()||cmbCodigoResponsableTurno.getSelectionModel().isEmpty()||
                        cmbCodigoPaciente.getSelectionModel().isEmpty()){
                    JOptionPane.showMessageDialog(null, "Debe llenar todos los campos.");
                }else if(validarValorCita == false){
                    JOptionPane.showMessageDialog(null, "Ha llenado un campo incorrectamente");
                    txtValorCita.setText("");
                }else if(txtValorCita.getText().length()>=13){
                    JOptionPane.showMessageDialog(null, "El precio ingresado no es válido.");
                    txtValorCita.setText("");
                }else{
                    guardar();
                    desactivarControles();
                    tblTurnos.setDisable(false);
                    btnNuevo.setText("Nuevo");
                    btnEliminar.setText("Eliminar");
                    btnEditar.setDisable(false);
                    btnReporte.setDisable(false);
                    tipoDeOperacion = operaciones.NINGUNO;
                    limpiarControles();
                    cargarDatos();
                }
                break;
        }
    }
    
    public void reporte(){
        switch(tipoDeOperacion){
            case ACTUALIZAR:
                desactivarControles();
                btnEditar.setText("Editar");
                btnReporte.setText("Reporte");
                btnNuevo.setDisable(false);
                btnEliminar.setDisable(false);
                tipoDeOperacion = operaciones.NINGUNO;
                limpiarControles();
                if(tblTurnos.getSelectionModel().getSelectedItem() != null){
                    seleccionarElemento();
                }
                break;
            /*case NINGUNO:
                imprimirReporte();
                limpiarControles();
                tipoDeOperacion = operaciones.NINGUNO;
                if(tblTurnos.getSelectionModel().getSelectedItem() != null){
                    seleccionarElemento();
                }
                break;*/
        }
    }
    
    public void guardar(){
        Turno registro = new Turno();
        registro.setFechaTurno(fechaTurno.getSelectedDate());
        registro.setFechaCita(fechaCita.getSelectedDate());
        registro.setValorCita(Double.parseDouble(txtValorCita.getText()));
        registro.setCodigoMedicoEspecialidad(((MedicoEspecialidad)cmbCodigoMedicoEspecialidad.getSelectionModel().getSelectedItem()).getCodigoMedicoEspecialidad());
        registro.setCodigoResponsableTurno(((ResponsableTurno)cmbCodigoResponsableTurno.getSelectionModel().getSelectedItem()).getCodigoResponsableTurno());
        registro.setCodigoPaciente(((Paciente)cmbCodigoPaciente.getSelectionModel().getSelectedItem()).getCodigoPaciente());
        try{
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_AgregarTurno(?,?,?,?,?,?)}");
            procedimiento.setDate(1, new java.sql.Date(registro.getFechaTurno().getTime()));
            procedimiento.setDate(2, new java.sql.Date(registro.getFechaCita().getTime()));
            procedimiento.setDouble(3, registro.getValorCita());
            procedimiento.setInt(4, registro.getCodigoMedicoEspecialidad());
            procedimiento.setInt(5, registro.getCodigoResponsableTurno());
            procedimiento.setInt(6, registro.getCodigoPaciente());
            procedimiento.execute();
            listaTurno.add(registro);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void editar(){
        switch(tipoDeOperacion){
            case NINGUNO:
                if(tblTurnos.getSelectionModel().getSelectedItem() != null){
                    btnEditar.setText("Actualizar");
                    btnReporte.setText("Cancelar");
                    btnNuevo.setDisable(true);
                    btnEliminar.setDisable(true);
                    activarControles();
                    tipoDeOperacion = operaciones.ACTUALIZAR;
                    seleccionarElemento();
                }else{
                    JOptionPane.showMessageDialog(null, "Debe seleccionar un elemento.");
                }break;
            case ACTUALIZAR:
                boolean validarValorCita = validacion(txtValorCita.getText());
                if(fechaTurno.selectedDateProperty()==null||fechaCita.selectedDateProperty()==null|| txtValorCita.getText().equals("") || 
                        cmbCodigoMedicoEspecialidad.getSelectionModel().isEmpty()||cmbCodigoResponsableTurno.getSelectionModel().isEmpty()
                        ||cmbCodigoPaciente.getSelectionModel().isEmpty()){
                    JOptionPane.showMessageDialog(null, "Debe llenar todos los campos.");
                }else if(validarValorCita == false){
                    JOptionPane.showMessageDialog(null, "Ha llenado un campo incorrectamente");
                    txtValorCita.setText("");
                }else if(txtValorCita.getText().length()!=13){
                    JOptionPane.showMessageDialog(null, "El precio ingresado no es válido");
                    txtValorCita.setText("");
                }else{
                    actualizar();
                    desactivarControles();
                    btnEditar.setText("Editar");
                    btnReporte.setText("Reporte");
                    btnNuevo.setDisable(false);
                    btnEliminar.setDisable(false);
                    tipoDeOperacion = operaciones.NINGUNO;
                    limpiarControles();
                    cargarDatos();
                }break;
        }
    }
    
    public void actualizar(){
        try{
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_EditarTurno(?,?,?,?)}");
            Turno registro = (Turno)tblTurnos.getSelectionModel().getSelectedItem();
            registro.setFechaTurno(fechaTurno.getSelectedDate());
            registro.setFechaCita(fechaCita.getSelectedDate());
            registro.setValorCita(Double.parseDouble(txtValorCita.getText()));
            procedimiento.setInt(1, registro.getCodigoTurno());
            procedimiento.setDate(2, new java.sql.Date(registro.getFechaTurno().getTime()));
            procedimiento.setDate(3, new java.sql.Date(registro.getFechaCita().getTime()));
            procedimiento.setDouble(4, registro.getValorCita());
            procedimiento.execute();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public boolean validacion(String ValorCita){
        double caracter = 0;
        try{
            caracter = Double.parseDouble(ValorCita);
            return true;
        }catch(Exception e){
            return false;
        }
    }
    
    public void desactivarControles(){
        fechaTurno.setDisable(true);
        fechaCita.setDisable(true);
        txtValorCita.setEditable(false);
        cmbCodigoMedicoEspecialidad.setDisable(true);
        cmbCodigoResponsableTurno.setDisable(true);
        cmbCodigoPaciente.setDisable(true);
    }
    
    public void activarControles(){
        fechaTurno.setDisable(false);
        fechaCita.setDisable(false);
        txtValorCita.setEditable(true);
        cmbCodigoMedicoEspecialidad.setDisable(false);
        cmbCodigoResponsableTurno.setDisable(false);
        cmbCodigoPaciente.setDisable(false);
    }
    
    public void limpiarControles(){
        fechaCita.selectedDateProperty().set(null);
        fechaTurno.selectedDateProperty().set(null);
        txtValorCita.setText("");
        cmbCodigoMedicoEspecialidad.getSelectionModel().select(null);
        cmbCodigoResponsableTurno.getSelectionModel().select(null);
        cmbCodigoPaciente.getSelectionModel().select(null);
    }
    
    public Principal getEscenarioPrincipal() {
        return escenarioPrincipal;
    }

    public void setEscenarioPrincipal(Principal escenarioPrincipal) {
        this.escenarioPrincipal = escenarioPrincipal;
    }
    
    public void menuPrincipal(){
        escenarioPrincipal.menuPrincipal();
    }
}
