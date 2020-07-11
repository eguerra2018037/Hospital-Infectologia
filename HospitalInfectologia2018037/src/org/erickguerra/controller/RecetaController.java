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
import org.erickguerra.bean.ControlCita;
import org.erickguerra.bean.Receta;
import org.erickguerra.db.Conexion;
import org.erickguerra.sistema.Principal;

public class RecetaController implements Initializable{
    private Principal escenarioPrincipal;
    private enum operaciones{NUEVO, NINGUNO, CANCELAR, ELIMINAR, ACTUALIZAR, EDITAR, GUARDAR}
    private operaciones tipoDeOperacion = operaciones.NINGUNO;
    private ObservableList<Receta> listaRecetas;
    private ObservableList<ControlCita> listaControlCitas;
    
    @FXML TextField txtDescripcion;
    @FXML ComboBox cmbCodigoControlCita;
    @FXML TableView tblRecetas;
    @FXML TableColumn colCodigoReceta;
    @FXML TableColumn colDescripcion;
    @FXML TableColumn colCodigoControlCita;
    @FXML Button btnNuevo;
    @FXML Button btnEliminar;
    @FXML Button btnEditar;
    @FXML Button btnReporte;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cargarDatos();
        cmbCodigoControlCita.setItems(getControlCitas());
    }
    
    public void cargarDatos(){
        tblRecetas.setItems(getRecetas());
        colCodigoReceta.setCellValueFactory(new PropertyValueFactory<Receta, Integer>("codigoReceta"));
        colDescripcion.setCellValueFactory(new PropertyValueFactory<Receta, String>("descripcionReceta"));
        colCodigoControlCita.setCellValueFactory(new PropertyValueFactory<Receta, Integer>("codigoControlCita"));
    }
    
    public ObservableList<Receta> getRecetas(){
        ArrayList<Receta> lista = new ArrayList<Receta>();
        try{
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_ListarRecetas()}");
            ResultSet resultado = procedimiento.executeQuery();
            while(resultado.next()){
                lista.add(new Receta(resultado.getInt("codigoReceta"),
                    resultado.getString("descripcionReceta"),
                    resultado.getInt("codigoControlCita")));
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return listaRecetas = FXCollections.observableList(lista);
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
    
    public Receta buscarReceta(int codigoReceta){
        Receta resultado = null;
        try{
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_BuscarReceta(?)}");
            procedimiento.setInt(1, codigoReceta);
            ResultSet registro = procedimiento.executeQuery();
            while(registro.next()){
                resultado = new Receta(registro.getInt("codigoReceta"),
                    registro.getString("descripcionReceta"),
                    registro.getInt("codigoControlCita"));
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return resultado;
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
    
    public void seleccionarElemento(){
        if(tblRecetas.getSelectionModel().isEmpty()){
            tblRecetas.getSelectionModel();
        }else{
            txtDescripcion.setText(String.valueOf(((Receta)tblRecetas.getSelectionModel().getSelectedItem()).getDescripcionReceta()));
            cmbCodigoControlCita.getSelectionModel().select(buscarControlCita(((Receta)tblRecetas.getSelectionModel().getSelectedItem()).getCodigoControlCita()));
        }
    }
    
    public void nuevo(){
        switch(tipoDeOperacion){
            case NINGUNO:
                btnNuevo.setText("Guardar");
                btnEliminar.setText("Cancelar");
                btnEditar.setDisable(true);
                tblRecetas.setDisable(true);
                limpiarControles();
                activarControles();
                tipoDeOperacion = operaciones.GUARDAR;
                break;
            case GUARDAR:
                if(txtDescripcion.getText().equals("") || cmbCodigoControlCita.getSelectionModel().isEmpty()){
                    JOptionPane.showMessageDialog(null, "Debe llenar todos los campos.");
                }else if(txtDescripcion.getText().length()>150){
                    JOptionPane.showMessageDialog(null, "Ha excedido el límite de caracteres");
                    txtDescripcion.setText("");
                }else{
                    guardar();
                        btnNuevo.setText("Nuevo");
                        btnEliminar.setText("Eliminar");
                        btnEditar.setDisable(false);
                        tblRecetas.setDisable(false);
                        limpiarControles();
                        desactivarControles();
                        cargarDatos();
                        tipoDeOperacion = operaciones.NINGUNO;
                        if(tblRecetas.getSelectionModel().getSelectedItem()!=null){
                            seleccionarElemento();}
                }break;
        }
    }
    
    public void guardar(){
        Receta resultado = new Receta();
        resultado.setDescripcionReceta(txtDescripcion.getText());
        resultado.setCodigoControlCita(((ControlCita)cmbCodigoControlCita.getSelectionModel().getSelectedItem()).getCodigoControlCita());
        try{
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_AgregarReceta(?,?)}");
            procedimiento.setString(1, resultado.getDescripcionReceta());
            procedimiento.setInt(2, resultado.getCodigoControlCita());
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
                tblRecetas.setDisable(false);
                desactivarControles();
                limpiarControles();
                tipoDeOperacion = operaciones.NINGUNO;
                break;
            case ACTUALIZAR:
                btnNuevo.setDisable(false);
                btnEditar.setText("Editar");
                btnEliminar.setText("Eliminar");
                limpiarControles();
                desactivarControles();
                tblRecetas.setDisable(false);
                tipoDeOperacion = operaciones.NINGUNO;
                break;
            default:
                seleccionarElemento();
                if(tblRecetas.getSelectionModel().getSelectedItem() != null){
                    int respuesta = JOptionPane.showConfirmDialog(null, "¿Está seguro de que desea eliminar el registro?", "Eliminar Receta", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                    if(respuesta == JOptionPane.YES_OPTION){
                        try{
                            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_EliminarReceta(?)}");
                            procedimiento.setInt(1, ((Receta)tblRecetas.getSelectionModel().getSelectedItem()).getCodigoReceta());
                            procedimiento.execute();
                            listaControlCitas.remove(tblRecetas.getSelectionModel().getSelectedIndex());
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
                if(tblRecetas.getSelectionModel().getSelectedItem() != null){
                    btnNuevo.setDisable(true);
                    btnEditar.setText("Actualizar");
                    btnEliminar.setText("Cancelar");
                    activarControles();
                    cmbCodigoControlCita.setDisable(true);
                    tblRecetas.setDisable(true);
                    tipoDeOperacion = operaciones.ACTUALIZAR;
                }else{
                    JOptionPane.showMessageDialog(null, "Debe seleccionar un elemento.");
                }break;
            case ACTUALIZAR:
                if(txtDescripcion.getText().equals("")){
                    JOptionPane.showMessageDialog(null, "Debe llenar todos los campos.");
                }else if(txtDescripcion.getText().length()>150){
                    JOptionPane.showMessageDialog(null, "Ha excedido el límite de caracteres");
                    txtDescripcion.setText("");
                }else{
                    actualizar();
                    btnEditar.setText("Editar");
                    btnEliminar.setText("Eliminar");
                    btnNuevo.setDisable(false);
                    tblRecetas.setDisable(false);
                    limpiarControles();
                    desactivarControles();
                    cargarDatos();
                    tipoDeOperacion = operaciones.NINGUNO;
                    if(tblRecetas.getSelectionModel().getSelectedItem()!=null){
                        seleccionarElemento();}
                }break;
        }
    }
    
    public void actualizar(){
        try{
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_EditarReceta(?,?)}");
            Receta resultado = (Receta)tblRecetas.getSelectionModel().getSelectedItem();
            resultado.setDescripcionReceta(txtDescripcion.getText());
            procedimiento.setInt(1, resultado.getCodigoReceta());
            procedimiento.setString(2, resultado.getDescripcionReceta());
            procedimiento.execute();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    /*public void imprimirReporte(){
        Map parametros = new HashMap();
        parametros.put("ReporteReceta.jasper", null);
        
    }*/
    
    public void limpiarControles(){
        switch(tipoDeOperacion){
            case NINGUNO:
                txtDescripcion.setText("");
                cmbCodigoControlCita.getSelectionModel().select(null);
                tblRecetas.getSelectionModel().clearSelection();
                break;
        }
    }
    
    public void activarControles(){
        txtDescripcion.setEditable(true);
        cmbCodigoControlCita.setDisable(false);
    }
    
    public void desactivarControles(){
        txtDescripcion.setEditable(false);
        cmbCodigoControlCita.setDisable(true);
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
