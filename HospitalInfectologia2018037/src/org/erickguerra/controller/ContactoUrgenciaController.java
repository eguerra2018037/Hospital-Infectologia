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
import org.erickguerra.bean.ContactoUrgencia;
import org.erickguerra.bean.Paciente;
import org.erickguerra.db.Conexion;
import org.erickguerra.sistema.Principal;

public class ContactoUrgenciaController implements Initializable{
    private Principal escenarioPrincipal;
    private enum operaciones{NUEVO,GUARDAR,ELIMINAR,EDITAR,ACTUALIZAR,CANCELAR,NINGUNO}
    private operaciones tipoDeOperacion = operaciones.NINGUNO;
    private ObservableList<ContactoUrgencia> listaContactoUrgencia;
    private ObservableList<Paciente> listaPaciente;
    
    @FXML private TextField txtNombres;
    @FXML private TextField txtApellidos;
    @FXML private TextField txtNumeroContacto;
    @FXML private ComboBox cmbCodigoPaciente;
    @FXML private TableView tblContactoUrgencia;
    @FXML private TableColumn colCodigoContacto;
    @FXML private TableColumn colNombres;
    @FXML private TableColumn colApellidos;
    @FXML private TableColumn colNumeroContacto;
    @FXML private TableColumn colCodigoPaciente;
    @FXML private Button btnNuevo;
    @FXML private Button btnEliminar;
    @FXML private Button btnEditar;
    @FXML private Button btnReporte;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cargarDatos();
        cmbCodigoPaciente.setItems(getPacientes());
    }

    public void cargarDatos(){
        tblContactoUrgencia.setItems(getContactoUrgencia());
        colCodigoContacto.setCellValueFactory(new PropertyValueFactory<ContactoUrgencia, Integer>("codigoContactoUrgencia"));
        colNombres.setCellValueFactory(new PropertyValueFactory<ContactoUrgencia, String>("nombres"));
        colApellidos.setCellValueFactory(new PropertyValueFactory<ContactoUrgencia, String>("apellidos"));
        colNumeroContacto.setCellValueFactory(new PropertyValueFactory<ContactoUrgencia, String>("numeroContacto"));
        colCodigoPaciente.setCellValueFactory(new PropertyValueFactory<ContactoUrgencia, Integer>("codigoPaciente"));
    }
    
    public ObservableList<ContactoUrgencia> getContactoUrgencia(){
        ArrayList<ContactoUrgencia> lista = new ArrayList<ContactoUrgencia>();
        try{
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_ListarContactos}");
            ResultSet resultado = procedimiento.executeQuery();
            while(resultado.next()){
                lista.add(new ContactoUrgencia(resultado.getInt("codigoContactoUrgencia"),
                    resultado.getString("nombres"),
                    resultado.getString("apellidos"),
                    resultado.getString("numeroContacto"),
                    resultado.getInt("codigoPaciente")));
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return listaContactoUrgencia = FXCollections.observableList(lista);
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
        if(tblContactoUrgencia.getSelectionModel().isEmpty()){
            tblContactoUrgencia.getSelectionModel();
        }else{
            txtNombres.setText(String.valueOf(((ContactoUrgencia)tblContactoUrgencia.getSelectionModel().getSelectedItem()).getNombres()));
            txtApellidos.setText(String.valueOf(((ContactoUrgencia)tblContactoUrgencia.getSelectionModel().getSelectedItem()).getApellidos()));
            txtNumeroContacto.setText(String.valueOf(((ContactoUrgencia)tblContactoUrgencia.getSelectionModel().getSelectedItem()).getNumeroContacto()));
            cmbCodigoPaciente.getSelectionModel().select(buscarPaciente(((ContactoUrgencia)tblContactoUrgencia.getSelectionModel().getSelectedItem()).getCodigoPaciente()));
        }
    }
    
    public ContactoUrgencia buscarContactoUrgencia(int codigoContactoUrgencia){
        ContactoUrgencia resultado = null;
        try{
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_BuscarContacto(?)}");
            procedimiento.setInt(1, codigoContactoUrgencia);
            ResultSet registro = procedimiento.executeQuery();
            while(registro.next()){
                resultado = new ContactoUrgencia(registro.getInt("codigoContactoUrgencia"),
                    registro.getString("nombres"),
                    registro.getString("apellidos"),
                    registro.getString("numeroContacto"),
                    registro.getInt("codigoPaciente"));
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
                tblContactoUrgencia.setDisable(false);
                btnNuevo.setText("Nuevo");
                btnEliminar.setText("Eliminar");
                btnEditar.setDisable(false);
                btnReporte.setDisable(false);
                tipoDeOperacion = operaciones.NINGUNO;
                limpiarControles();
                if(tblContactoUrgencia.getSelectionModel().getSelectedItem() != null)
                    seleccionarElemento();
                break;
            default:
                seleccionarElemento();
                if(tblContactoUrgencia.getSelectionModel().getSelectedItem() != null){
                    int respuesta = JOptionPane.showConfirmDialog(null, "¿Está seguro de que desea eliminar el registro?", "Eliminar Contacto Urgencia", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                    if(respuesta == JOptionPane.YES_OPTION){
                        try{
                            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_EliminarContacto(?)}");
                            procedimiento.setInt(1, ((ContactoUrgencia)tblContactoUrgencia.getSelectionModel().getSelectedItem()).getCodigoContactoUrgencia());
                            procedimiento.execute();
                            listaContactoUrgencia.remove(tblContactoUrgencia.getSelectionModel().getSelectedIndex());
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
                tblContactoUrgencia.setDisable(true);
                btnNuevo.setText("Guardar");
                btnEliminar.setText("Cancelar");
                btnEditar.setDisable(true);
                btnReporte.setDisable(true);
                tipoDeOperacion = operaciones.GUARDAR;
                break;
            case GUARDAR:
                boolean validar = validacionNumero(txtNumeroContacto.getText());
                if(txtNombres.getText().equals("") || txtApellidos.getText().equals("") || txtNumeroContacto.getText().equals("") 
                        || cmbCodigoPaciente.getSelectionModel().isEmpty()){
                    JOptionPane.showMessageDialog(null, "Debe llenar todos los campos.");
                }else if(validar == false){
                    JOptionPane.showMessageDialog(null, "Ha llenado el campo incorrectamente");
                    txtNumeroContacto.setText("");
                }else if(txtNombres.getText().length()>100){
                    JOptionPane.showMessageDialog(null, "Ha excedido el número de caracteres");
                    txtNombres.setText("");
                }else if(txtApellidos.getText().length()>100){
                    JOptionPane.showMessageDialog(null, "Ha excedido el número de caracteres");
                    txtApellidos.setText("");
                }else if(txtNumeroContacto.getText().length()!=8){
                    JOptionPane.showMessageDialog(null, "El número ingresado no es válido");
                    txtNumeroContacto.setText("");
                }else{
                    guardar();
                    desactivarControles();
                    tblContactoUrgencia.setDisable(false);
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
                if(tblContactoUrgencia.getSelectionModel().getSelectedItem() != null){
                    seleccionarElemento();
                }
                break;
        }
    }
    
    public void guardar(){
        ContactoUrgencia registro = new ContactoUrgencia();
        registro.setNombres(txtNombres.getText());
        registro.setApellidos(txtApellidos.getText());
        registro.setNumeroContacto(txtNumeroContacto.getText());
        registro.setCodigoPaciente(((Paciente)cmbCodigoPaciente.getSelectionModel().getSelectedItem()).getCodigoPaciente());
        try{
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_AgregarContacto(?,?,?,?)}");
            procedimiento.setString(1, registro.getNombres());
            procedimiento.setString(2, registro.getApellidos());
            procedimiento.setString(3, registro.getNumeroContacto());
            procedimiento.setInt(4, registro.getCodigoPaciente());
            procedimiento.execute();
            listaContactoUrgencia.add(registro);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void editar(){
        switch(tipoDeOperacion){
            case NINGUNO:
                if(tblContactoUrgencia.getSelectionModel().getSelectedItem() != null){
                    btnEditar.setText("Actualizar");
                    btnReporte.setText("Cancelar");
                    btnNuevo.setDisable(true);
                    btnEliminar.setDisable(true);
                    cmbCodigoPaciente.setDisable(true);
                    activarControles();
                    cmbCodigoPaciente.setDisable(true);
                    tipoDeOperacion = operaciones.ACTUALIZAR;
                    seleccionarElemento();
                }else{
                    JOptionPane.showMessageDialog(null, "Debe seleccionar un elemento.");
                }break;
            case ACTUALIZAR:
                 boolean validar = validacionNumero(txtNumeroContacto.getText());
                if(txtNombres.getText().equals("") || txtApellidos.getText().equals("") 
                        || txtNumeroContacto.getText().equals("")){
                    JOptionPane.showMessageDialog(null, "Debe llenar todos los campos.");
                }else if(validar == false){
                    JOptionPane.showMessageDialog(null, "Ha llenado el campo incorrectamente");
                    txtNumeroContacto.setText("");
                }else if(txtNombres.getText().length()>100){
                    JOptionPane.showMessageDialog(null, "Ha excedido el número de caracteres");
                    txtNombres.setText("");
                }else if(txtApellidos.getText().length()>100){
                    JOptionPane.showMessageDialog(null, "Ha excedido el número de caracteres");
                    txtApellidos.setText("");
                }else if(txtNumeroContacto.getText().length()!=8){
                    JOptionPane.showMessageDialog(null, "El número ingresado no es válido.");
                    txtNumeroContacto.setText("");
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
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_EditarContacto(?,?,?,?)}");
            ContactoUrgencia registro = (ContactoUrgencia) tblContactoUrgencia.getSelectionModel().getSelectedItem();
            registro.setNombres(txtNombres.getText());
            registro.setApellidos(txtApellidos.getText());
            registro.setNumeroContacto(txtNumeroContacto.getText());
            procedimiento.setInt(1, registro.getCodigoContactoUrgencia());
            procedimiento.setString(2, registro.getNombres());
            procedimiento.setString(3, registro.getApellidos());
            procedimiento.setString(4, registro.getNumeroContacto());
            procedimiento.execute();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public boolean validacionNumero(String numero){
        int caracter = 0;
        try{
            caracter = Integer.parseInt(numero);
            return true;
        }catch(Exception e){
            return false;
        }
    }
    
    public void activarControles(){
        txtNombres.setEditable(true);
        txtApellidos.setEditable(true);
        txtNumeroContacto.setEditable(true);
        cmbCodigoPaciente.setDisable(false);
    }
    
    public void desactivarControles(){
        txtNombres.setEditable(false);
        txtApellidos.setEditable(false);
        txtNumeroContacto.setEditable(false);
        cmbCodigoPaciente.setDisable(true);
    }
    
    public void limpiarControles(){
        switch(tipoDeOperacion){
            case NINGUNO:
                txtNombres.setText("");
                txtApellidos.setText("");
                txtNumeroContacto.setText("");
                cmbCodigoPaciente.getSelectionModel().select(null);
                tblContactoUrgencia.getSelectionModel().clearSelection();
                break;
        }
    }
    
    public Principal getEscenarioPrincipal(){
        return escenarioPrincipal;
    }
    
    public void setEscenarioPrincipal(Principal escenarioPrincipal){
        this.escenarioPrincipal = escenarioPrincipal;
    }
    
    public void menuPrincipal(){
        escenarioPrincipal.menuPrincipal();
    }
    
    public void ventanaPacientes(){
        escenarioPrincipal.ventanaPacientes();
    }
}