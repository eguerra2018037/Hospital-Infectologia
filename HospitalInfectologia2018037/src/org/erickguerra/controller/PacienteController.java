package org.erickguerra.controller;

import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.ResourceBundle;
import eu.schudt.javafx.controls.calendar.DatePicker;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
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
import org.erickguerra.bean.Paciente;
import org.erickguerra.db.Conexion;
import org.erickguerra.report.GenerarReporte;
import org.erickguerra.sistema.Principal;


public class PacienteController implements Initializable{
    private enum operaciones {EDITAR, ELIMINAR, GUARDAR, NUEVO, ACTUALIZAR, CANCELAR, NINGUNO}
    private Principal escenarioPrincipal;
    private operaciones tipoDeOperacion = operaciones.NINGUNO;
    private ObservableList<Paciente> listaPaciente;
    private DatePicker fecha;
    
    @FXML private TextField txtDPI;
    @FXML private TextField txtApellidos;
    @FXML private TextField txtNombres;
    @FXML private GridPane grpFecha;
    @FXML private TextField txtEdad;
    @FXML private TextField txtDireccion;
    @FXML private TextField txtOcupacion;
    //@FXML private TextField txtSexo;
    @FXML private TableView tblPacientes;
    @FXML private TableColumn colCodigoPaciente;
    @FXML private TableColumn colDPI;
    @FXML private TableColumn colApellidos;
    @FXML private TableColumn colNombres;
    @FXML private TableColumn colFechaNacimiento;
    @FXML private TableColumn colEdad;
    @FXML private TableColumn colDireccion;
    @FXML private TableColumn colOcupacion;
    @FXML private TableColumn colSexo;
    @FXML private ComboBox cmbSexo;
    @FXML private Button btnNuevo;
    @FXML private Button btnEliminar;
    @FXML private Button btnEditar;
    @FXML private Button btnReporte;
    
    @Override
    public void initialize(URL location, ResourceBundle resources){
        cargarDatos();
        fecha = new DatePicker(Locale.ENGLISH);
        fecha.setDateFormat(new SimpleDateFormat("yyy-MM-dd"));
        fecha.getCalendarView().todayButtonTextProperty().set("Today");
        fecha.getCalendarView().setShowWeeks(false);
        fecha.getStylesheets().add("/org/erickguerra/resource/DatePicker.css");
        grpFecha.add(fecha,0,0);
        fecha.setDisable(true);
        cmbSexo.setItems(FXCollections.observableArrayList("Masculino", "Femenino", "Otro"));
    }
    
    public void cargarDatos(){
        tblPacientes.setItems(getPacientes());
        colCodigoPaciente.setCellValueFactory(new PropertyValueFactory<Paciente, Integer>("codigoPaciente"));
        colDPI.setCellValueFactory(new PropertyValueFactory<Paciente, String>("DPI"));
        colApellidos.setCellValueFactory(new PropertyValueFactory<Paciente, String>("apellidos"));
        colNombres.setCellValueFactory(new PropertyValueFactory<Paciente, String>("nombres"));
        colFechaNacimiento.setCellValueFactory(new PropertyValueFactory<Paciente, Date>("fechaNacimiento"));
        colEdad.setCellValueFactory(new PropertyValueFactory<Paciente, Integer>("edad"));
        colDireccion.setCellValueFactory(new PropertyValueFactory<Paciente, String>("direccion"));
        colOcupacion.setCellValueFactory(new PropertyValueFactory<Paciente, String>("ocupacion"));
        colSexo.setCellValueFactory(new PropertyValueFactory<Paciente, String>("sexo"));
    }
    
    public ObservableList<Paciente> getPacientes(){
        ArrayList<Paciente> lista = new ArrayList<Paciente>();
            try{
                PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_ListarPacientes}");
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
        if(tblPacientes.getSelectionModel().isEmpty()){
            tblPacientes.getSelectionModel();
        }else{
            txtDPI.setText(String.valueOf(((Paciente) tblPacientes.getSelectionModel().getSelectedItem()).getDPI()));
            txtApellidos.setText(String.valueOf(((Paciente) tblPacientes.getSelectionModel().getSelectedItem()).getApellidos()));
            txtNombres.setText(String.valueOf(((Paciente) tblPacientes.getSelectionModel().getSelectedItem()).getNombres()));        
            fecha.selectedDateProperty().set(((Paciente)tblPacientes.getSelectionModel().getSelectedItem()).getFechaNacimiento());
            txtEdad.setText(String.valueOf(((Paciente) tblPacientes.getSelectionModel().getSelectedItem()).getEdad()));
            txtDireccion.setText(String.valueOf(((Paciente) tblPacientes.getSelectionModel().getSelectedItem()).getDireccion()));
            txtOcupacion.setText(String.valueOf(((Paciente) tblPacientes.getSelectionModel().getSelectedItem()).getOcupacion()));
            //txtSexo.setText(String.valueOf(((Paciente) tblPacientes.getSelectionModel().getSelectedItem()).getSexo()));
            cmbSexo.getSelectionModel().select(String.valueOf(((Paciente)tblPacientes.getSelectionModel().getSelectedItem()).getSexo()));
        }
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
    
    public void eliminar(){
        switch(tipoDeOperacion){
            case GUARDAR:
                desactivarControles();
                tblPacientes.setDisable(false);
                btnNuevo.setText("Nuevo");
                btnEliminar.setText("Eliminar");
                btnEditar.setDisable(false);
                btnReporte.setDisable(false);
                tipoDeOperacion = operaciones.NINGUNO;
                limpiarControles();
                if(tblPacientes.getSelectionModel().getSelectedItem() != null){
                    seleccionarElemento();
                }
                break;
            default:
                seleccionarElemento();
                if(tblPacientes.getSelectionModel().getSelectedItem() != null){
                    int respuesta = JOptionPane.showConfirmDialog(null, "¿Está seguro de que desea eliminar el registro?", "Eliminar Paciente", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                    if(respuesta == JOptionPane.YES_OPTION){
                        try{
                            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_EliminarPaciente(?)}");
                            procedimiento.setInt(1, ((Paciente) tblPacientes.getSelectionModel().getSelectedItem()).getCodigoPaciente());
                            procedimiento.execute();
                            listaPaciente.remove(tblPacientes.getSelectionModel().getSelectedIndex());
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
                tblPacientes.setDisable(true);
                btnNuevo.setText("Guardar");
                btnEliminar.setText("Cancelar");
                btnEditar.setDisable(true);
                btnReporte.setDisable(true);
                tipoDeOperacion = operaciones.GUARDAR;
                break;
            case GUARDAR:
                boolean validarDPI = validacionDPIFecha(txtDPI.getText());
                if(txtDPI.getText().equals("") || txtApellidos.getText().equals("") || txtNombres.getText().equals("") || 
                        fecha.selectedDateProperty().get() == null || txtDireccion.getText().equals("") || txtOcupacion.getText().equals("") 
                        || /*txtSexo.getText().equals("") ||*/ cmbSexo.getSelectionModel().isEmpty()){
                    JOptionPane.showMessageDialog(null, "Debe llenar todos los campos.");
                }else if(validarDPI == false){
                    JOptionPane.showMessageDialog(null, "Ha llenado un campo incorrectamente");
                    txtDPI.setText("");
                }else if(txtDPI.getText().length()!=13){
                    JOptionPane.showMessageDialog(null, "El DPI ingresado no es válido.");
                    txtDPI.setText("");
                }else if(txtApellidos.getText().length()>100){
                    JOptionPane.showMessageDialog(null, "Ha excedido la cantidad de caracteres");
                    txtApellidos.setText("");
                }else if(txtNombres.getText().length()>100){
                    JOptionPane.showMessageDialog(null, "Ha excedido la cantidad de caracteres");
                    txtNombres.setText("");
                }else if(txtDireccion.getText().length()>150){
                    JOptionPane.showMessageDialog(null, "Ha excedido la cantidad de caracteres");
                    txtDireccion.setText("");
                }else if(txtOcupacion.getText().length()>50){
                    JOptionPane.showMessageDialog(null, "Ha excedido la cantidad de caracteres");
                    txtOcupacion.setText("");
                /*}else if(txtSexo.getText().length()>15){
                    JOptionPane.showMessageDialog(null, "Ha excedido la cantidad de caracteres");
                    txtSexo.setText("");*/
                }else{
                    guardar();
                    desactivarControles();
                    tblPacientes.setDisable(false);
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
                if(tblPacientes.getSelectionModel().getSelectedItem() != null){
                    seleccionarElemento();
                }
                break;
            case NINGUNO:
                imprimirReporte();
                limpiarControles();
                tipoDeOperacion = operaciones.NINGUNO;
                if(tblPacientes.getSelectionModel().getSelectedItem() != null){
                    seleccionarElemento();
                }
                break;
        }
    }
    
    public void guardar(){
        Paciente registro = new Paciente();
        registro.setDPI(txtDPI.getText());
        registro.setApellidos(txtApellidos.getText());
        registro.setNombres(txtNombres.getText());
        registro.setFechaNacimiento(fecha.getSelectedDate());
        registro.setDireccion(txtDireccion.getText());
        registro.setOcupacion(txtOcupacion.getText());
        //registro.setSexo(txtSexo.getText());
        registro.setSexo(cmbSexo.getSelectionModel().getSelectedItem().toString());
        try{
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_AgregarPaciente(?,?,?,?,?,?,?)}");
            procedimiento.setString(1, registro.getDPI());
            procedimiento.setString(2, registro.getApellidos());
            procedimiento.setString(3, registro.getNombres());
            procedimiento.setDate(4, new java.sql.Date(registro.getFechaNacimiento().getTime()));
            procedimiento.setString(5, registro.getDireccion());
            procedimiento.setString(6, registro.getOcupacion());
            procedimiento.setString(7, registro.getSexo());
            procedimiento.execute();
            listaPaciente.add(registro);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void editar(){
        switch(tipoDeOperacion){
            case NINGUNO:
                if(tblPacientes.getSelectionModel().getSelectedItem() != null){
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
                boolean validarDPI = validacionDPIFecha(txtDPI.getText());
                if(txtDPI.getText().equals("") || txtApellidos.getText().equals("") || txtNombres.getText().equals("") || 
                        fecha.selectedDateProperty() == null || txtDireccion.getText().equals("") || txtOcupacion.getText().equals("") 
                        || /*txtSexo.getText().equals("") ||*/ cmbSexo.getSelectionModel().isEmpty()){
                    JOptionPane.showMessageDialog(null, "Debe llenar todos los campos.");
                }else if(validarDPI == false){
                    JOptionPane.showMessageDialog(null, "Ha llenado un campo incorrectamente");
                    txtDPI.setText("");
                }else if(txtDPI.getText().length()!=13){
                    JOptionPane.showMessageDialog(null, "El DPI ingresado no es válido");
                    txtDPI.setText("");
                }else if(txtApellidos.getText().length()>100){
                    JOptionPane.showMessageDialog(null, "Ha excedido la cantidad de caracteres");
                    txtApellidos.setText("");
                }else if(txtNombres.getText().length()>100){
                    JOptionPane.showMessageDialog(null, "Ha excedido la cantidad de caracteres");
                    txtNombres.setText("");
                }else if(txtDireccion.getText().length()>150){
                    JOptionPane.showMessageDialog(null, "Ha excedido la cantidad de caracteres");
                    txtDireccion.setText("");
                }else if(txtOcupacion.getText().length()>50){
                    JOptionPane.showMessageDialog(null, "Ha excedido la cantidad de caracteres");
                    txtOcupacion.setText("");
                /*}else if(txtSexo.getText().length()>15){
                    JOptionPane.showMessageDialog(null, "Ha excedido la cantidad de caracteres");
                    txtSexo.setText("");*/
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
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_EditarPaciente(?,?,?,?,?,?,?,?,?)}");
            Paciente registro = (Paciente)tblPacientes.getSelectionModel().getSelectedItem();
            registro.setDPI(txtDPI.getText());
            registro.setApellidos(txtApellidos.getText());
            registro.setNombres(txtNombres.getText());
            registro.setFechaNacimiento(fecha.getSelectedDate());
            registro.setDireccion(txtDireccion.getText());
            registro.setOcupacion(txtOcupacion.getText());
            //registro.setSexo(txtSexo.getText());
            registro.setSexo(cmbSexo.getSelectionModel().getSelectedItem().toString());
            procedimiento.setInt(1, registro.getCodigoPaciente());
            procedimiento.setString(2, registro.getDPI());
            procedimiento.setString(3, registro.getApellidos());
            procedimiento.setString(4, registro.getNombres());
            procedimiento.setDate(5, new java.sql.Date(registro.getFechaNacimiento().getTime()));
            procedimiento.setInt(6, registro.getEdad());
            procedimiento.setString(7, registro.getDireccion());
            procedimiento.setString(8, registro.getOcupacion());
            procedimiento.setString(9, registro.getSexo());
            procedimiento.execute();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public boolean validacionDPIFecha(String DPI){
        long caracter = 0;
        try{
            caracter = Long.parseLong(DPI);
            return true;
        }catch(Exception e){
            return false;
        }
    }
    
    public void imprimirReporte(){
        Map parametros = new HashMap();
        parametros.put("codigoPaciente", null);
        GenerarReporte.mostrarReporte("ReportePacientes.jasper", "Reporte de Pacientes", parametros);
    }
    
    public void desactivarControles(){
        txtDPI.setEditable(false);
        txtApellidos.setEditable(false);
        txtNombres.setEditable(false);
        fecha.setDisable(true);
        txtEdad.setEditable(false);
        txtDireccion.setEditable(false);
        txtOcupacion.setEditable(false);
        //txtSexo.setEditable(false);
        cmbSexo.setDisable(true);
    }
    
    public void activarControles(){
        txtDPI.setEditable(true);
        txtApellidos.setEditable(true);
        txtNombres.setEditable(true);
        fecha.setDisable(false);
        txtEdad.setEditable(false);
        txtDireccion.setEditable(true);
        txtOcupacion.setEditable(true);
        //txtSexo.setEditable(true);
        cmbSexo.setDisable(false);
    }
    
    public void limpiarControles(){
        switch(tipoDeOperacion){
            case NINGUNO:
                txtDPI.setText("");
                txtApellidos.setText("");
                txtNombres.setText("");
                fecha.selectedDateProperty().set(null);
                txtEdad.setText("");
                txtDireccion.setText("");
                txtOcupacion.setText("");
                //txtSexo.setText("");
                tblPacientes.getSelectionModel().clearSelection();
                cmbSexo.getSelectionModel().select(null);
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
    
    public void ventanaContactoUrgencia(){
        escenarioPrincipal.ventanaContactoUrgencia();
    }
    
}
