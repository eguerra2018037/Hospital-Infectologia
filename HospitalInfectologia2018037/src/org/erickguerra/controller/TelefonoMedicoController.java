package org.erickguerra.controller;

import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
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
import javax.swing.JOptionPane;
import org.erickguerra.bean.Medico;
import org.erickguerra.bean.TelefonoMedico;
import org.erickguerra.db.Conexion;
import org.erickguerra.sistema.Principal;

public class TelefonoMedicoController implements Initializable{
    private Principal escenarioPrincipal;
    private enum operaciones{NUEVO,GUARDAR,ELIMINAR,EDITAR,ACTUALIZAR,CANCELAR,NINGUNO}
    private operaciones tipoDeOperacion = operaciones.NINGUNO;
    private ObservableList<Medico> listaMedico;
    private ObservableList<TelefonoMedico> listaTelefonoMedico;
    
    @FXML private TextField txtTelefonoPersonal;
    @FXML private TextField txtTelefonoTrabajo;
    @FXML private ComboBox cmbCodigoMedico;
    @FXML private TableView tblTelefonoMedico;
    @FXML private TableColumn colCodigoTelefono;
    @FXML private TableColumn colTelefonoPersonal;
    @FXML private TableColumn colTelefonoTrabajo;
    @FXML private TableColumn colCodigoMedico;
    @FXML private Button btnNuevo;
    @FXML private Button btnEliminar;
    @FXML private Button btnEditar;
    @FXML private Button btnReporte;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cargarDatos();
        cmbCodigoMedico.setItems(getMedicos());
    }
    
    public void cargarDatos(){
        tblTelefonoMedico.setItems(getTelefonoMedicos());
        colCodigoTelefono.setCellValueFactory(new PropertyValueFactory<TelefonoMedico, Integer>("codigoTelefonoMedico"));
        colTelefonoPersonal.setCellValueFactory(new PropertyValueFactory<TelefonoMedico, String>("telefonoPersonal"));
        colTelefonoTrabajo.setCellValueFactory(new PropertyValueFactory<TelefonoMedico, String>("telefonoTrabajo"));
        colCodigoMedico.setCellValueFactory(new PropertyValueFactory<TelefonoMedico, Integer>("codigoMedico"));
    }
    
    public ObservableList<TelefonoMedico> getTelefonoMedicos(){
        ArrayList<TelefonoMedico> lista = new ArrayList<TelefonoMedico>();
        try{
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_ListarTelefonoMedicos}");
            ResultSet resultado = procedimiento.executeQuery();
            while(resultado.next()){
                lista.add(new TelefonoMedico(resultado.getInt("codigoTelefonoMedico"),
                    resultado.getString("telefonoPersonal"),
                    resultado.getString("telefonoTrabajo"),
                    resultado.getInt("codigoMedico")));
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return listaTelefonoMedico = FXCollections.observableList(lista);
    }
    
    public ObservableList<Medico> getMedicos(){
        ArrayList<Medico> lista = new ArrayList<Medico>();
            try{
                PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_ListarMedicos}");
                ResultSet resultado = procedimiento.executeQuery();
                while(resultado.next()){
                    lista.add(new Medico(resultado.getInt("codigoMedico"),
                        resultado.getInt("licenciaMedica"),
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
            return listaMedico = FXCollections.observableList(lista);
    }
    
    public Medico buscarMedico(int codigoMedico){
        Medico resultado = null;
            try{
                PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_BuscarMedico(?)}");
                procedimiento.setInt(1, codigoMedico);
                ResultSet registro = procedimiento.executeQuery();
                while(registro.next()){
                    resultado = new Medico(registro.getInt("codigoMedico"),
                            registro.getInt("licenciaMedica"),
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
    
    public void seleccionarElemento(){
        if(tblTelefonoMedico.getSelectionModel().isEmpty()){
            tblTelefonoMedico.getSelectionModel();
        }else{
        txtTelefonoPersonal.setText(String.valueOf(((TelefonoMedico) tblTelefonoMedico.getSelectionModel().getSelectedItem()).getTelefonoPersonal()));
        txtTelefonoTrabajo.setText(String.valueOf(((TelefonoMedico) tblTelefonoMedico.getSelectionModel().getSelectedItem()).getTelefonoTrabajo()));
        cmbCodigoMedico.getSelectionModel().select(buscarMedico(((TelefonoMedico)tblTelefonoMedico.getSelectionModel().getSelectedItem()).getCodigoMedico()));
        }
    }
    
    public TelefonoMedico buscarTelefonoMedico(int codigoTelefonoMedico){
        TelefonoMedico resultado = null;
        try{
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_BuscarTelefonoMedico(?)}");
            procedimiento.setInt(1, codigoTelefonoMedico);
            ResultSet registro = procedimiento.executeQuery();
            while(registro.next()){
                resultado = new TelefonoMedico(registro.getInt("codigoTelefonoMedico"),
                    registro.getString("telefonoPersonal"),
                    registro.getString("telefonoTrabajo"),
                    registro.getInt("codigoMedico"));
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
                tblTelefonoMedico.setDisable(false);
                btnNuevo.setText("Nuevo");
                btnEliminar.setText("Eliminar");
                btnEditar.setDisable(false);
                btnReporte.setDisable(false);
                tipoDeOperacion = operaciones.NINGUNO;
                limpiarControles();
                if(tblTelefonoMedico.getSelectionModel().getSelectedItem() != null){
                    seleccionarElemento();
                }
                break;
            default:
                seleccionarElemento();
                if(tblTelefonoMedico.getSelectionModel().getSelectedItem() != null){
                    int respuesta = JOptionPane.showConfirmDialog(null, "¿Está seguro de que desea eliminar el registro?", "Eliminar Teléfono Médico", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                    if(respuesta == JOptionPane.YES_OPTION){
                        try{
                            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_EliminarTelefonoMedico(?)}");
                            procedimiento.setInt(1, ((TelefonoMedico)tblTelefonoMedico.getSelectionModel().getSelectedItem()).getCodigoTelefonoMedico());
                            procedimiento.execute();
                            listaTelefonoMedico.remove(tblTelefonoMedico.getSelectionModel().getSelectedIndex());
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
    
    public void nuevo(){
        switch(tipoDeOperacion){
            case NINGUNO:
                activarControles();
                limpiarControles();
                tblTelefonoMedico.setDisable(true);
                btnNuevo.setText("Guardar");
                btnEliminar.setText("Cancelar");
                btnEditar.setDisable(true);
                btnReporte.setDisable(true);
                cmbCodigoMedico.setDisable(false);
                tipoDeOperacion = operaciones.GUARDAR;
                break;
            case GUARDAR:
                boolean validarTP = validacionTelefonos(txtTelefonoPersonal.getText());
                boolean validarTT = validacionTelefonos(txtTelefonoTrabajo.getText());
                if(txtTelefonoPersonal.getText().equals("") || txtTelefonoTrabajo.getText().equals("")
                        || cmbCodigoMedico.getSelectionModel().isEmpty()){
                    JOptionPane.showMessageDialog(null, "Debe llenar todos los campos.");
                }else if(validarTP == false){
                    JOptionPane.showMessageDialog(null, "Ha llenado un campo incorrectamente");
                    txtTelefonoPersonal.setText("");
                }else if(validarTT == false){
                    JOptionPane.showMessageDialog(null, "Ha llenado un campo incorrectamente.");
                    txtTelefonoTrabajo.setText("");
                }else if(txtTelefonoPersonal.getText().length()!=8){
                    JOptionPane.showMessageDialog(null, "El número ingresado no es válido.");
                    txtTelefonoPersonal.setText("");
                }else if(txtTelefonoTrabajo.getText().length()!=8){
                    JOptionPane.showMessageDialog(null, "El número ingresado no es válido.");
                    txtTelefonoTrabajo.setText("");
                }else{
                    guardar();
                    desactivarControles();
                    tblTelefonoMedico.setDisable(false);
                    btnNuevo.setText("Nuevo");
                    btnEliminar.setText("Eliminar");
                    btnEditar.setDisable(false);
                    btnReporte.setDisable(false);
                    tipoDeOperacion = operaciones.NINGUNO;
                    limpiarControles();
                    cargarDatos();
                }break;
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
                if(tblTelefonoMedico.getSelectionModel().getSelectedItem() != null){
                    seleccionarElemento();
                }
                break;
        }
    }
    
    public void guardar(){
        TelefonoMedico registro = new TelefonoMedico();
        registro.setTelefonoPersonal(txtTelefonoPersonal.getText());
        registro.setTelefonoTrabajo(txtTelefonoTrabajo.getText());
        registro.setCodigoMedico(((Medico)cmbCodigoMedico.getSelectionModel().getSelectedItem()).getCodigoMedico());
        try{
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_AgregarTelefonoMedico(?,?,?)}");
            procedimiento.setString(1, registro.getTelefonoPersonal());
            procedimiento.setString(2, registro.getTelefonoTrabajo());
            procedimiento.setInt(3, registro.getCodigoMedico());
            procedimiento.execute();
            listaTelefonoMedico.add(registro);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void editar(){
        switch(tipoDeOperacion){
            case NINGUNO:
                if(tblTelefonoMedico.getSelectionModel().getSelectedItem() != null){
                    btnEditar.setText("Actualizar");
                    btnReporte.setText("Cancelar");
                    btnNuevo.setDisable(true);
                    btnEliminar.setDisable(true);
                    cmbCodigoMedico.setDisable(true);
                    activarControles();
                    cmbCodigoMedico.setDisable(true);
                    tipoDeOperacion = operaciones.ACTUALIZAR;
                    seleccionarElemento();
                }else{
                    JOptionPane.showMessageDialog(null, "Debe seleccionar un elemento.");
                }break;
            case ACTUALIZAR:
                boolean validarTP = validacionTelefonos(txtTelefonoPersonal.getText());
                boolean validarTT = validacionTelefonos(txtTelefonoTrabajo.getText());
                if(txtTelefonoPersonal.getText().equals("") || txtTelefonoTrabajo.getText().equals("")){
                    JOptionPane.showMessageDialog(null, "Debe llenar todos los campos.");
                }else if(validarTP == false){
                    JOptionPane.showMessageDialog(null, "Ha llenado un campo incorrectamente");
                    txtTelefonoPersonal.setText("");
                }else if(validarTT == false){
                    JOptionPane.showMessageDialog(null, "Ha llenado un campo incorrectamente.");
                    txtTelefonoTrabajo.setText("");
                }else if(txtTelefonoPersonal.getText().length()!=8){
                    JOptionPane.showMessageDialog(null, "El número ingresado no es válido.");
                    txtTelefonoPersonal.setText("");
                }else if(txtTelefonoTrabajo.getText().length()!=8){
                    JOptionPane.showMessageDialog(null, "El número ingresado no es válido.");
                    txtTelefonoTrabajo.setText("");
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
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_EditarTelefonoMedico(?,?,?)}");
            TelefonoMedico registro = (TelefonoMedico) tblTelefonoMedico.getSelectionModel().getSelectedItem();
            registro.setTelefonoPersonal(txtTelefonoPersonal.getText());
            registro.setTelefonoTrabajo(txtTelefonoTrabajo.getText());
            procedimiento.setInt(1, registro.getCodigoTelefonoMedico());
            procedimiento.setString(2, registro.getTelefonoPersonal());
            procedimiento.setString(3, registro.getTelefonoTrabajo());
            procedimiento.execute();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public boolean validacionTelefonos(String telefono){
        int caracter = 0;
        try{
            caracter = Integer.parseInt(telefono);
            return true;
        }catch(Exception e){
            return false;
        }
    }
    
    public void activarControles(){
        txtTelefonoPersonal.setEditable(true);
        txtTelefonoTrabajo.setEditable(true);
        cmbCodigoMedico.setDisable(false);
    }
    
    public void desactivarControles(){
        txtTelefonoPersonal.setEditable(false);
        txtTelefonoTrabajo.setEditable(false);
        cmbCodigoMedico.setDisable(true);
    }
    
    public void limpiarControles(){
        switch(tipoDeOperacion){
            case NINGUNO:
                txtTelefonoPersonal.setText("");
                txtTelefonoTrabajo.setText("");
                tblTelefonoMedico.getSelectionModel();
                cmbCodigoMedico.getSelectionModel().select(null);
                break;
        }
    }
    
    public Principal getEscenarioPrincipal(){
        return escenarioPrincipal;
    }
    
    public void setEscenarioPrincipal(Principal escenarioPrincipal){
        this.escenarioPrincipal = escenarioPrincipal;
    }
    
    public void ventanaMedicos(){
        escenarioPrincipal.ventanaMedicos();
    }
}