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
import org.erickguerra.bean.Area;
import org.erickguerra.bean.Cargo;
import org.erickguerra.bean.ResponsableTurno;
import org.erickguerra.db.Conexion;
import org.erickguerra.sistema.Principal;

public class ResponsableTurnoController implements Initializable{
    private enum operaciones{NUEVO, EDITAR, ACTUALIZAR, NINGUNO, GUARDAR, ELIMINAR, CANCELAR}
    private Principal escenarioPrincipal;
    private operaciones tipoDeOperacion = operaciones.NINGUNO;
    private ObservableList<ResponsableTurno> listaResponsablesTurno;
    private ObservableList<Area> listaArea;
    private ObservableList<Cargo> listaCargos;
    
    @FXML private TextField txtNombres;
    @FXML private TextField txtApellidos;
    @FXML private TextField txtTelefonoPersonal;
    @FXML private ComboBox cmbCodigoArea;
    @FXML private ComboBox cmbCodigoCargo;
    @FXML private TableView tblResponsableTurno;
    @FXML private TableColumn colCodigoResponsableTurno;
    @FXML private TableColumn colNombres;
    @FXML private TableColumn colApellidos;
    @FXML private TableColumn colTelefonoPersonal;
    @FXML private TableColumn colCodigoArea;
    @FXML private TableColumn colCodigoCargo;
    @FXML private Button btnNuevo;
    @FXML private Button btnEliminar;
    @FXML private Button btnEditar;
    @FXML private Button btnReporte;
    
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cargarDatos();
        cmbCodigoArea.setItems(getAreas());
        cmbCodigoCargo.setItems(getCargos());
    }
    
    public void cargarDatos(){
        tblResponsableTurno.setItems(getResponsablesTurno());
        colCodigoResponsableTurno.setCellValueFactory(new PropertyValueFactory<ResponsableTurno, Integer>("codigoResponsableTurno"));
        colNombres.setCellValueFactory (new PropertyValueFactory<ResponsableTurno, String>("nombreResponsable"));
        colApellidos.setCellValueFactory (new PropertyValueFactory<ResponsableTurno, String>("apellidosResponsable"));
        colTelefonoPersonal.setCellValueFactory(new PropertyValueFactory<ResponsableTurno, String>("telefonoPersonal"));
        colCodigoArea.setCellValueFactory(new PropertyValueFactory<ResponsableTurno, Integer>("codigoArea"));
        colCodigoCargo.setCellValueFactory(new PropertyValueFactory<ResponsableTurno, Integer>("codigoCargo"));
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
    
    public ObservableList<Area> getAreas(){
        ArrayList<Area> lista = new ArrayList<Area>();
        try{
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_ListarAreas}");
            ResultSet resultado = procedimiento.executeQuery();
            while(resultado.next()){
                lista.add(new Area(resultado.getInt("codigoArea"),
                    resultado.getString("nombreArea")));
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return listaArea = FXCollections.observableList(lista);
    }
    
    public ObservableList<Cargo> getCargos(){
        ArrayList<Cargo> lista = new ArrayList<Cargo>();
        try{
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_ListarCargos}");
            ResultSet resultado = procedimiento.executeQuery();
            while(resultado.next()){
                lista.add(new Cargo(resultado.getInt("codigoCargo"),
                    resultado.getString("nombreCargo")));
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return listaCargos = FXCollections.observableList(lista);
    }
    
    public Area buscarArea(int codigoArea){
        Area resultado = null;
        try{
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_BuscarArea(?)}");
            procedimiento.setInt(1,codigoArea);
            ResultSet registro = procedimiento.executeQuery();
            while(registro.next()){
                resultado = new Area(registro.getInt("codigoArea"),
                    registro.getString("nombreArea"));
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return resultado;
    }
    
    public Cargo buscarCargo(int codigoCargo){
        Cargo resultado = null;
        try{
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_BuscarCargo(?)}");
            procedimiento.setInt(1,codigoCargo);
            ResultSet registro = procedimiento.executeQuery();
            while(registro.next()){
                resultado = new Cargo(registro.getInt("codigoCargo"),
                    registro.getString("nombreCargo"));
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return resultado;
    }
    
    public void seleccionarElemento(){
        if(tblResponsableTurno.getSelectionModel().isEmpty()){
            tblResponsableTurno.getSelectionModel();
        }else{
            txtNombres.setText(String.valueOf(((ResponsableTurno) tblResponsableTurno.getSelectionModel().getSelectedItem()).getNombreResponsable()));
            txtApellidos.setText(String.valueOf(((ResponsableTurno) tblResponsableTurno.getSelectionModel().getSelectedItem()).getApellidosResponsable()));
            txtTelefonoPersonal.setText(String.valueOf(((ResponsableTurno)tblResponsableTurno.getSelectionModel().getSelectedItem()).getTelefonoPersonal()));
            cmbCodigoArea.getSelectionModel().select(buscarArea(((ResponsableTurno)tblResponsableTurno.getSelectionModel().getSelectedItem()).getCodigoArea()));
            cmbCodigoCargo.getSelectionModel().select(buscarCargo(((ResponsableTurno)tblResponsableTurno.getSelectionModel().getSelectedItem()).getCodigoCargo()));
        }
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
    
    public void eliminar(){
        switch(tipoDeOperacion){
            case GUARDAR:
                desactivarControles();
                tblResponsableTurno.setDisable(false);
                btnNuevo.setText("Nuevo");
                btnEliminar.setText("Eliminar");
                btnEditar.setDisable(false);
                btnReporte.setDisable(false);
                tipoDeOperacion = operaciones.NINGUNO;
                limpiarControles();
                if(tblResponsableTurno.getSelectionModel().getSelectedItem() != null){
                    seleccionarElemento();
                }
                break;
            default:
                seleccionarElemento();
                if(tblResponsableTurno.getSelectionModel().getSelectedItem()!= null){
                    int respuesta = JOptionPane.showConfirmDialog(null, "¿Està seguro de que desea eliminar el registro?", "Eliminsr Responsable Turno", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                    if(respuesta == JOptionPane.YES_OPTION){
                        try{
                            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_EliminarResponsableTurno(?)}");
                            procedimiento.setInt(1, ((ResponsableTurno)tblResponsableTurno.getSelectionModel().getSelectedItem()).getCodigoResponsableTurno());
                            procedimiento.execute();
                            listaResponsablesTurno.remove(tblResponsableTurno.getSelectionModel().getSelectedIndex());
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
                tblResponsableTurno.setDisable(true);
                btnNuevo.setText("Guardar");
                btnEliminar.setText("Cancelar");
                btnEditar.setDisable(true);
                btnReporte.setDisable(true);
                tipoDeOperacion = operaciones.GUARDAR;
                break;
            case GUARDAR:
                boolean validarTP = validacionTelefonoPersonal(txtTelefonoPersonal.getText());
                if(txtNombres.getText().equals("") || txtApellidos.getText().equals("") || txtTelefonoPersonal.getText().equals("") || 
                        cmbCodigoArea.getSelectionModel().isEmpty() || cmbCodigoCargo.getSelectionModel().isEmpty()){
                    JOptionPane.showMessageDialog(null, "Debe llenar todos los campos.");
                }else if(validarTP == false){
                    JOptionPane.showMessageDialog(null, "Ha llenado un campo incorrectamente");
                    txtTelefonoPersonal.setText("");
                }else if(txtNombres.getText().length()>75){
                    JOptionPane.showMessageDialog(null, "Ha excedido el límite de caracteres.");
                    txtNombres.setText("");
                }else if(txtApellidos.getText().length()>45){
                    JOptionPane.showMessageDialog(null, "Ha excedido la cantidad de caracteres.");
                    txtApellidos.setText("");
                }else if(txtTelefonoPersonal.getText().length()!=8){
                    JOptionPane.showMessageDialog(null, "El número ingresado no es válido.");
                    txtTelefonoPersonal.setText("");
                }else{
                    guardar();
                    desactivarControles();
                    tblResponsableTurno.setDisable(false);
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
                if(tblResponsableTurno.getSelectionModel().getSelectedItem() != null){
                    seleccionarElemento();
                }
                break;
            /*case NINGUNO:
                imprimirReporte();
                limpiarControles();
                tipoDeOperacion = operaciones.NINGUNO;
                if(tblResponsableTurno.getSelectionModel().getSelectedItem()!=null){
                    seleccionarElemento();
                }*/
        }
    }
    
    public void guardar(){
        ResponsableTurno registro = new ResponsableTurno();
        registro.setNombreResponsable(txtNombres.getText());
        registro.setApellidosResponsable(txtApellidos.getText());
        registro.setTelefonoPersonal(txtTelefonoPersonal.getText());
        registro.setCodigoArea(((Area)cmbCodigoArea.getSelectionModel().getSelectedItem()).getCodigoArea());
        registro.setCodigoCargo(((Cargo)cmbCodigoCargo.getSelectionModel().getSelectedItem()).getCodigoCargo());
        try{
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_AgregarResponsableTurno(?,?,?,?,?)}");
            procedimiento.setString(1, registro.getNombreResponsable());
            procedimiento.setString(2, registro.getApellidosResponsable());
            procedimiento.setString(3, registro.getTelefonoPersonal());
            procedimiento.setInt(4, registro.getCodigoArea());
            procedimiento.setInt(5, registro.getCodigoCargo());
            procedimiento.execute();
            listaResponsablesTurno.add(registro);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void editar(){
        switch(tipoDeOperacion){
            case NINGUNO:
                if(tblResponsableTurno.getSelectionModel().getSelectedItem()!=null){
                    btnEditar.setText("Actualizar");
                    btnReporte.setText("Cancelar");
                    btnNuevo.setDisable(true);
                    btnEliminar.setDisable(true);
                    cmbCodigoArea.setDisable(true);
                    cmbCodigoCargo.setDisable(true);
                    activarControles();
                    cmbCodigoArea.setDisable(true);
                    cmbCodigoCargo.setDisable(true);
                    tipoDeOperacion = operaciones.ACTUALIZAR;
                    seleccionarElemento();
                }else{
                    JOptionPane.showMessageDialog(null, "Debe seleccionar un elemento.");
                }break;
            case ACTUALIZAR:
                boolean validarTP = validacionTelefonoPersonal(txtTelefonoPersonal.getText());
                if(txtNombres.getText().equals("") || txtApellidos.getText().equals("") || txtTelefonoPersonal.getText().equals("")){
                    JOptionPane.showMessageDialog(null, "Debe llenar todos los campos.");
                }else if(validarTP == false){
                    JOptionPane.showMessageDialog(null, "Ha llenado un campo incorrectamente");
                    txtTelefonoPersonal.setText("");
                }else if(txtNombres.getText().length()>75){
                    JOptionPane.showMessageDialog(null, "Ha excedido el límite de caracteres.");
                    txtNombres.setText("");
                }else if(txtApellidos.getText().length()>45){
                    JOptionPane.showMessageDialog(null, "Ha excedido la cantidad de caracteres.");
                    txtApellidos.setText("");
                }else if(txtTelefonoPersonal.getText().length()!=8){
                    JOptionPane.showMessageDialog(null, "El número ingresado no es válido.");
                    txtTelefonoPersonal.setText("");
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
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_EditarResponsableTurno(?,?,?,?)}");
            ResponsableTurno registro = (ResponsableTurno) tblResponsableTurno.getSelectionModel().getSelectedItem();
            registro.setNombreResponsable(txtNombres.getText());
            registro.setApellidosResponsable(txtApellidos.getText());
            registro.setTelefonoPersonal(txtTelefonoPersonal.getText());
            procedimiento.setInt(1, registro.getCodigoResponsableTurno());
            procedimiento.setString(2, registro.getNombreResponsable());
            procedimiento.setString(3, registro.getApellidosResponsable());
            procedimiento.setString(4, registro.getTelefonoPersonal());
            procedimiento.execute();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public boolean validacionTelefonoPersonal(String telefono){
        int caracter = 0;
        try{
            caracter = Integer.parseInt(telefono);
            return true;
        }catch(Exception e){
            return false;
        }
    }
    
    public void activarControles(){
        txtNombres.setEditable(true);
        txtApellidos.setEditable(true);
        txtTelefonoPersonal.setEditable(true);
        cmbCodigoArea.setDisable(false);
        cmbCodigoCargo.setDisable(false);
    }
        
    public void desactivarControles(){
        txtNombres.setEditable(false);
        txtApellidos.setEditable(false);
        txtTelefonoPersonal.setEditable(false);
        cmbCodigoArea.setDisable(true);
        cmbCodigoCargo.setDisable(true);
    }
        
    public void limpiarControles(){
         switch(tipoDeOperacion){
            case NINGUNO:
                txtNombres.setText("");
                txtApellidos.setText("");
                txtTelefonoPersonal.setText("");
                cmbCodigoArea.getSelectionModel().select(null);
                cmbCodigoCargo.getSelectionModel().select(null);
                tblResponsableTurno.getSelectionModel().clearSelection();
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
}

