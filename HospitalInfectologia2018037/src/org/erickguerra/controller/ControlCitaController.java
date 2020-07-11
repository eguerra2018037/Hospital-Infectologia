package org.erickguerra.controller;

import eu.schudt.javafx.controls.calendar.DatePicker;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
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
import org.erickguerra.bean.ControlCita;
import org.erickguerra.bean.Medico;
import org.erickguerra.bean.Paciente;
import org.erickguerra.db.Conexion;
import org.erickguerra.report.GenerarReporte;
import org.erickguerra.sistema.Principal;

public class ControlCitaController implements Initializable{
    private Principal escenarioPrincipal;
    private enum operaciones{NUEVO, EDITAR, CANCELAR, ACTUALIZAR, GUARDAR, NINGUNO, ELIMINAR}
    private operaciones tipoDeOperacion = operaciones.NINGUNO;
    private ObservableList<ControlCita> listaControlCitas;
    private ObservableList<Medico> listaMedicos;
    private ObservableList<Paciente> listaPacientes;
    private DatePicker fecha;
    
    @FXML TextField txtHoraInicio;
    @FXML TextField txtHoraFin;
    @FXML GridPane grpFecha;
    @FXML ComboBox cmbCodigoMedico;
    @FXML ComboBox cmbCodigoPaciente;
    @FXML TableView tblControlCitas;
    @FXML TableColumn colCodigoControlCita;
    @FXML TableColumn colFecha;
    @FXML TableColumn colHoraInicio;
    @FXML TableColumn colHoraFin;
    @FXML TableColumn colCodigoMedico;
    @FXML TableColumn colCodigoPaciente;
    @FXML Button btnNuevo;
    @FXML Button btnEliminar;
    @FXML Button btnEditar;
    @FXML Button btnReporte;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cargarDatos();
        cmbCodigoMedico.setItems(getMedicos());
        cmbCodigoPaciente.setItems(getPacientes());
        fecha = new DatePicker(Locale.ENGLISH);
        fecha.setDateFormat(new SimpleDateFormat("yyy-MM-dd"));
        fecha.getCalendarView().todayButtonTextProperty().set("Today");
        fecha.getCalendarView().setShowWeeks(false);
        fecha.getStylesheets().add(("/org/erickguerra/resource/DatePicker.css"));
        grpFecha.add(fecha, 0, 0);
        fecha.setDisable(true);
    }
    
    public void cargarDatos(){
        tblControlCitas.setItems(getControlCitas());
        colCodigoControlCita.setCellValueFactory(new PropertyValueFactory<ControlCita, Integer>("codigoControlCita"));
        colFecha.setCellValueFactory(new PropertyValueFactory<ControlCita, Date>("fecha"));
        colHoraInicio.setCellValueFactory(new PropertyValueFactory<ControlCita, String>("horaInicio"));
        colHoraFin.setCellValueFactory(new PropertyValueFactory<ControlCita, String>("horaFin"));
        colCodigoMedico.setCellValueFactory(new PropertyValueFactory<ControlCita, Integer>("codigoMedico"));
        colCodigoPaciente.setCellValueFactory(new PropertyValueFactory<ControlCita, Integer>("codigoPaciente"));
    }
    
    public ObservableList<ControlCita> getControlCitas(){
        ArrayList<ControlCita> lista = new ArrayList<ControlCita>();
        try{
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_ListarControlCitas()}");
            ResultSet resultado = procedimiento.executeQuery();
            while(resultado.next()){
                lista.add(new ControlCita(resultado.getInt("codigoControlCita"),
                            resultado.getDate("fecha"),
                            resultado.getString("horaInicio"),
                            resultado.getString("horaFin"),
                            resultado.getInt("codigoMedico"),
                            resultado.getInt("codigoPaciente")));
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return listaControlCitas = FXCollections.observableList(lista);
    }
    
    public ObservableList<Medico> getMedicos(){
        ArrayList<Medico> lista = new ArrayList<Medico>();
        try{
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_ListarMedicos()}");
            ResultSet resultado = procedimiento.executeQuery();
            while(resultado.next()){
                lista.add(new Medico(resultado.getInt("codigoMedico"),
                            resultado.getLong("licenciaMedica"),
                            resultado.getString("nombres"),
                            resultado.getString("apellidos"),
                            resultado.getString("horaEntrada"),
                            resultado.getString("horaSalida"),
                            resultado.getInt("turnoMaximo"),
                            resultado.getString("sexo")));
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return listaMedicos = FXCollections.observableList(lista);
    }
    
    public ObservableList<Paciente> getPacientes(){
        ArrayList<Paciente> lista = new ArrayList<Paciente>();
        try{
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_ListarPacientes()}");
            ResultSet resultado = procedimiento.executeQuery();
            while(resultado.next()){
                lista.add(new Paciente(resultado.getInt("codigoPaciente"),
                            resultado.getString("DPI"),
                            resultado.getString("apellidos"),
                            resultado.getString("nombres"),
                            resultado.getDate("fechaNacimiento"),
                            resultado.getInt("edad"),
                            resultado.getString("direccion"),
                            resultado.getString("Ocupacion"),
                            resultado.getString("Sexo")));
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return listaPacientes = FXCollections.observableList(lista);
    }
    
    public ControlCita buscarControlCita(int codigoControlCita){
        ControlCita resultado = null;
        try{
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_BuscarControlCita(?)}");
            procedimiento.setInt(1, codigoControlCita);
            ResultSet registro = procedimiento.executeQuery();
            while(registro.next()){
                resultado = new ControlCita(registro.getInt("codigoControlCita"),
                    registro.getDate("fecha"),
                    registro.getString("horaInicio"),
                    registro.getString("horaFin"),
                    registro.getInt("codigoMedico"),
                    registro.getInt("codigoPaciente"));
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return resultado;
    }
    
    public Medico buscarMedico(int codigoMedico){
        Medico resultado = null;
        try{
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_BuscarMedico(?)}");
            procedimiento.setInt(1, codigoMedico);
            ResultSet registro = procedimiento.executeQuery();
            while(registro.next()){
                resultado = new Medico(registro.getInt("codigoMedico"),
                    registro.getLong("licenciaMedica"),
                    registro.getString("nombres"),
                    registro.getString("apellidos"),
                    registro.getString("horaEntrada"),
                    registro.getString("horaSalida"),
                    registro.getInt("turnoMaximo"),
                    registro.getString("sexo"));
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
    
    public void seleccionarElemento(){
        if(tblControlCitas.getSelectionModel().isEmpty()){
            tblControlCitas.getSelectionModel();
        }else{
            txtHoraInicio.setText(String.valueOf(((ControlCita)tblControlCitas.getSelectionModel().getSelectedItem()).getHoraInicio()));
            txtHoraFin.setText(String.valueOf(((ControlCita)tblControlCitas.getSelectionModel().getSelectedItem()).getHoraFin()));
            fecha.selectedDateProperty().set(((ControlCita)tblControlCitas.getSelectionModel().getSelectedItem()).getFecha());
            cmbCodigoMedico.getSelectionModel().select(buscarMedico(((ControlCita)tblControlCitas.getSelectionModel().getSelectedItem()).getCodigoMedico()));
            cmbCodigoPaciente.getSelectionModel().select(buscarPaciente(((ControlCita)tblControlCitas.getSelectionModel().getSelectedItem()).getCodigoPaciente()));
        }
    }
    
    public void nuevo(){
        switch(tipoDeOperacion){
            case NINGUNO:
                btnNuevo.setText("Guardar");
                btnEliminar.setText("Cancelar");
                btnEditar.setDisable(true);
                btnReporte.setDisable(true);
                tblControlCitas.setDisable(true);
                limpiarControles();
                activarControles();
                tipoDeOperacion = operaciones.GUARDAR;
                break;
            case GUARDAR:
                if(txtHoraInicio.getText().equals("") || txtHoraFin.getText().equals("") || cmbCodigoMedico.getSelectionModel().isEmpty()
                        || cmbCodigoPaciente.getSelectionModel().isEmpty() || fecha.selectedDateProperty() == null){
                    JOptionPane.showMessageDialog(null, "Debe llenar todos los campos.");
                }else if(txtHoraInicio.getText().length()!=8){
                    JOptionPane.showMessageDialog(null, "La hora ingresada no es válida \nEl formato correcto es HH:MM:SS");
                    txtHoraInicio.setText("");
                }else if(txtHoraFin.getText().length()!=8){
                    JOptionPane.showMessageDialog(null, "La hora ingresada no es válida \nEl formato correcto es HH:MM:SS");
                    txtHoraFin.setText("");
                }else if(Integer.parseInt(txtHoraInicio.getText().substring(0, 2))>24 || Integer.parseInt(txtHoraInicio.getText().substring(3, 5))>=60
                            || Integer.parseInt(txtHoraInicio.getText().substring(6, 8))>=60){
                    JOptionPane.showMessageDialog(null, "La hora ingresada no es válida");
                    txtHoraInicio.setText("");
                }else if(Integer.parseInt(txtHoraFin.getText().substring(0, 2))>24 || Integer.parseInt(txtHoraFin.getText().substring(3, 5))>=60
                            || Integer.parseInt(txtHoraFin.getText().substring(6, 8))>=60){
                    JOptionPane.showMessageDialog(null, "La hora ingresada no es válida.");
                    txtHoraFin.setText("");
                }else if(!txtHoraInicio.getText().substring(2, 3).equals(":") || !txtHoraInicio.getText().substring(5, 6).equals(":")){
                    JOptionPane.showMessageDialog(null, "La hora ingresada no es válida \nEl formato correcto es HH:MM:SS");
                    txtHoraInicio.setText("");
                }else if(!txtHoraFin.getText().substring(2, 3).equals(":") || !txtHoraFin.getText().substring(5, 6).equals(":")){
                    JOptionPane.showMessageDialog(null, "La hora ingresada no es válida \nEl formato correcto es HH:MM:SS");
                    txtHoraFin.setText("");
                }else{
                    boolean validarHoraInicio = validarHoras(txtHoraInicio.getText().substring(0, 2), txtHoraInicio.getText().substring(3, 5),
                            txtHoraInicio.getText().substring(6, 8));
                    boolean validarHoraFin = validarHoras(txtHoraFin.getText().substring(0, 2), txtHoraFin.getText().substring(3, 5),
                            txtHoraFin.getText().substring(6, 8));
                    if(validarHoraInicio == false){
                        JOptionPane.showMessageDialog(null, "La hora ingresada no es válida.");
                        txtHoraInicio.setText("");
                    }else if(validarHoraFin == false){
                        JOptionPane.showMessageDialog(null, "La hora ingresada no es válida.");
                        txtHoraFin.setText("");
                    }else{
                        guardar();
                        btnNuevo.setText("Nuevo");
                        btnEliminar.setText("Eliminar");
                        btnEditar.setDisable(false);
                        btnReporte.setDisable(false);
                        tblControlCitas.setDisable(false);
                        limpiarControles();
                        desactivarControles();
                        cargarDatos();
                        tipoDeOperacion = operaciones.NINGUNO;
                        if(tblControlCitas.getSelectionModel().getSelectedItem()!=null){
                            seleccionarElemento();}
                    }                    
                }break;
        }
    }
    
    public void guardar(){
        ControlCita resultado = new ControlCita();
        resultado.setHoraInicio(txtHoraInicio.getText());
        resultado.setHoraFin(txtHoraFin.getText());
        resultado.setFecha(fecha.getSelectedDate());
        resultado.setCodigoMedico(((Medico)cmbCodigoMedico.getSelectionModel().getSelectedItem()).getCodigoMedico());
        resultado.setCodigoPaciente(((Paciente)cmbCodigoPaciente.getSelectionModel().getSelectedItem()).getCodigoPaciente());
        try{
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_AgregarControlCita(?,?,?,?,?)}");
            procedimiento.setDate(1, new java.sql.Date(fecha.getSelectedDate().getTime()));
            procedimiento.setString(2, resultado.getHoraInicio());
            procedimiento.setString(3, resultado.getHoraFin());
            procedimiento.setInt(4, resultado.getCodigoMedico());
            procedimiento.setInt(5, resultado.getCodigoPaciente());
            procedimiento.execute();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void eliminar(){
        switch(tipoDeOperacion){
            case GUARDAR:
                btnNuevo.setText("Nuevo");
                btnEliminar.setText("Eliminar");
                btnEditar.setDisable(false);
                btnReporte.setDisable(false);
                tblControlCitas.setDisable(false);
                desactivarControles();
                limpiarControles();
                tipoDeOperacion = operaciones.NINGUNO;
                break;
            default:
                seleccionarElemento();
                if(tblControlCitas.getSelectionModel().getSelectedItem() != null){
                    int respuesta = JOptionPane.showConfirmDialog(null, "¿Está seguro de que desea eliminar el registro?", "Eliminar Control de Citas", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                    if(respuesta == JOptionPane.YES_OPTION){
                        try{
                            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_EliminarControlCita(?)}");
                            procedimiento.setInt(1, ((ControlCita)tblControlCitas.getSelectionModel().getSelectedItem()).getCodigoControlCita());
                            procedimiento.execute();
                            listaControlCitas.remove(tblControlCitas.getSelectionModel().getSelectedIndex());
                            limpiarControles();
                        }catch(Exception e){
                            e.printStackTrace();
                        }
                    }
                }else{
                    JOptionPane.showMessageDialog(null, "Debe seleccionar un elemento.");
                }
        }
    }
    
    public void editar(){
        switch(tipoDeOperacion){
            case NINGUNO:
                seleccionarElemento();
                if(tblControlCitas.getSelectionModel().getSelectedItem() != null){
                    btnNuevo.setDisable(true);
                    btnEliminar.setDisable(true);
                    btnEditar.setText("Actualizar");
                    btnReporte.setText("Cancelar");
                    activarControles();
                    cmbCodigoMedico.setDisable(true);
                    cmbCodigoPaciente.setDisable(true);
                    tblControlCitas.setDisable(true);
                    tipoDeOperacion = operaciones.ACTUALIZAR;
                }else{
                    JOptionPane.showMessageDialog(null, "Debe seleccionar un elemento.");
                }break;
            case ACTUALIZAR:
                if(txtHoraInicio.getText().equals("") || txtHoraFin.getText().equals("") || fecha.selectedDateProperty() == null){
                    JOptionPane.showMessageDialog(null, "Debe llenar todos los campos.");
                }else if(txtHoraInicio.getText().length()!=8){
                    JOptionPane.showMessageDialog(null, "La hora ingresada no es válida \nEl formato correcto es HH:MM:SS");
                    txtHoraInicio.setText("");
                }else if(txtHoraFin.getText().length()!=8){
                    JOptionPane.showMessageDialog(null, "La hora ingresada no es válida \nEl formato correcto es HH:MM:SS");
                    txtHoraFin.setText("");
                }else if(Integer.parseInt(txtHoraInicio.getText().substring(0, 2))>24 || Integer.parseInt(txtHoraInicio.getText().substring(3, 5))>=60
                            || Integer.parseInt(txtHoraInicio.getText().substring(6, 8))>=60){
                    JOptionPane.showMessageDialog(null, "La hora ingresada no es válida");
                    txtHoraInicio.setText("");
                }else if(Integer.parseInt(txtHoraFin.getText().substring(0, 2))>24 || Integer.parseInt(txtHoraFin.getText().substring(3, 5))>=60
                            || Integer.parseInt(txtHoraFin.getText().substring(6, 8))>=60){
                    JOptionPane.showMessageDialog(null, "La hora ingresada no es válida.");
                    txtHoraFin.setText("");
                }else if(!txtHoraInicio.getText().substring(2, 3).equals(":") || !txtHoraInicio.getText().substring(5, 6).equals(":")){
                    JOptionPane.showMessageDialog(null, "La hora ingresada no es válida \nEl formato correcto es HH:MM:SS");
                    txtHoraInicio.setText("");
                }else if(!txtHoraFin.getText().substring(2, 3).equals(":") || !txtHoraFin.getText().substring(5, 6).equals(":")){
                    JOptionPane.showMessageDialog(null, "La hora ingresada no es válida \nEl formato correcto es HH:MM:SS");
                    txtHoraFin.setText("");
                }else{
                    boolean validarHoraInicio = validarHoras(txtHoraInicio.getText().substring(0, 2), txtHoraInicio.getText().substring(3, 5),
                            txtHoraInicio.getText().substring(6, 8));
                    boolean validarHoraFin = validarHoras(txtHoraFin.getText().substring(0, 2), txtHoraFin.getText().substring(3, 5),
                            txtHoraFin.getText().substring(6, 8));
                    if(validarHoraInicio == false){
                        JOptionPane.showMessageDialog(null, "La hora ingresada no es válida.");
                        txtHoraInicio.setText("");
                    }else if(validarHoraFin == false){
                        JOptionPane.showMessageDialog(null, "La hora ingresada no es válida.");
                        txtHoraFin.setText("");
                    }else{
                        actualizar();
                        btnEditar.setText("Editar");
                        btnReporte.setText("Reporte");
                        btnNuevo.setDisable(false);
                        btnEliminar.setDisable(false);
                        tblControlCitas.setDisable(false);
                        limpiarControles();
                        desactivarControles();
                        cargarDatos();
                        tipoDeOperacion = operaciones.NINGUNO;
                        if(tblControlCitas.getSelectionModel().getSelectedItem()!=null){
                            seleccionarElemento();}
                    }                    
                }break;
        }
    }
    
    public void reporte(){
        switch(tipoDeOperacion){
            case ACTUALIZAR:
                btnNuevo.setDisable(false);
                btnEliminar.setDisable(false);
                btnEditar.setText("Editar");
                btnReporte.setText("Reporte");
                limpiarControles();
                desactivarControles();
                tblControlCitas.setDisable(false);
                tipoDeOperacion = operaciones.NINGUNO;
                break;
            case NINGUNO:
                imprimirReporte();
                limpiarControles();
                tipoDeOperacion = operaciones.NINGUNO;
                break;
        }
    }
    
    public void actualizar(){
        try{
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_EditarControlCita(?,?,?,?)}");
            ControlCita resultado = (ControlCita)tblControlCitas.getSelectionModel().getSelectedItem();
            resultado.setFecha(fecha.getSelectedDate());
            resultado.setHoraInicio(txtHoraInicio.getText());
            resultado.setHoraFin(txtHoraFin.getText());
            procedimiento.setInt(1, resultado.getCodigoControlCita());
            procedimiento.setDate(2, new java.sql.Date(resultado.getFecha().getTime()));
            procedimiento.setString(3, resultado.getHoraInicio());
            procedimiento.setString(4, resultado.getHoraFin());
            procedimiento.execute();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void imprimirReporte(){
        String respuesta = JOptionPane.showInputDialog(null, "Ingrese el código del paciente deseado");
        boolean validacionRespuesta = validarRespuesta(respuesta);
        if(validacionRespuesta == false){
            JOptionPane.showMessageDialog(null, "El código ingresado no es correcto");
        }else{
        Map parametros = new HashMap();
        parametros.put("codigoPaciente", Integer.parseInt(respuesta));
        GenerarReporte.mostrarReporte("ReporteControlCitas.jasper", "Reporte de Control de Citas", parametros);
        }
    }

    public boolean validarRespuesta(String respuesta){
        int resp = 0;
        try{
            resp = Integer.parseInt(respuesta);
            return true;
        }catch(Exception e){
            return false;
        }
    }
    
    public boolean validarHoras(String hora, String minuto, String segundo){
        int horas = 0;
        int minutos = 0;
        int segundos = 0;
        try{
            horas = Integer.parseInt(hora);
            minutos = Integer.parseInt(minuto);
            segundos = Integer.parseInt(segundo);
            return true;
        }catch(Exception e){
            return false;
        }
    }
    
    public void desactivarControles(){
        txtHoraInicio.setEditable(false);
        txtHoraFin.setEditable(false);
        cmbCodigoMedico.setDisable(true);
        cmbCodigoPaciente.setDisable(true);
        fecha.setDisable(true);
    }
    
    public void activarControles(){
        txtHoraInicio.setEditable(true);
        txtHoraFin.setEditable(true);
        cmbCodigoMedico.setDisable(false);
        cmbCodigoPaciente.setDisable(false);
        fecha.setDisable(false);
    }
    
    public void limpiarControles(){
        switch(tipoDeOperacion){
            case NINGUNO:
                txtHoraInicio.setText("");
                txtHoraFin.setText("");
                fecha.selectedDateProperty().set(null);
                cmbCodigoMedico.getSelectionModel().select(null);
                cmbCodigoPaciente.getSelectionModel().select(null);
                tblControlCitas.getSelectionModel().clearSelection();
                break;
        }
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
