package org.erickguerra.controller;

import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javax.swing.JOptionPane;
import org.erickguerra.sistema.Principal;
import org.erickguerra.bean.Horario;
import org.erickguerra.db.Conexion;
import org.erickguerra.report.GenerarReporte;

public class HorarioController implements Initializable{
    private Principal escenarioPrincipal;
    private enum operaciones{NINGUNO, GUARDAR, CANCELAR, ELIMINAR, ACTUALIZAR, EDITAR}
    private operaciones tipoDeOperacion = operaciones.NINGUNO;
    private ObservableList<Horario> listaHorarios;
    
    @FXML TextField txtHoraInicio;
    @FXML TextField txtHoraSalida;
    @FXML CheckBox cbLunes;
    @FXML CheckBox cbMartes;
    @FXML CheckBox cbMiercoles;
    @FXML CheckBox cbJueves;
    @FXML CheckBox cbViernes;
    @FXML TableView tblHorarios;
    @FXML TableColumn colCodigoHorario;
    @FXML TableColumn colHorarioInicio;
    @FXML TableColumn colHorarioSalida;
    @FXML TableColumn colLunes;
    @FXML TableColumn colMartes;
    @FXML TableColumn colMiercoles;
    @FXML TableColumn colJueves;
    @FXML TableColumn colViernes;
    @FXML Button btnNuevo;
    @FXML Button btnEliminar;
    @FXML Button btnEditar;
    @FXML Button btnReporte;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cargarDatos();
    }
    
    public void cargarDatos(){
        tblHorarios.setItems(getHorarios());
        colCodigoHorario.setCellValueFactory(new PropertyValueFactory<Horario, Integer>("codigoHorario"));
        colHorarioInicio.setCellValueFactory(new PropertyValueFactory<Horario, String>("horarioEntrada"));
        colHorarioSalida.setCellValueFactory(new PropertyValueFactory<Horario, String>("horarioSalida"));
        colLunes.setCellValueFactory(new PropertyValueFactory<Horario, Boolean>("lunes"));
        colMartes.setCellValueFactory(new PropertyValueFactory<Horario, Boolean>("martes"));
        colMiercoles.setCellValueFactory(new PropertyValueFactory<Horario, Boolean>("miercoles"));
        colJueves.setCellValueFactory(new PropertyValueFactory<Horario, Boolean>("jueves"));
        colViernes.setCellValueFactory(new PropertyValueFactory<Horario, Boolean>("viernes"));
    }
    
    public ObservableList<Horario> getHorarios(){
        ArrayList<Horario> lista = new ArrayList<Horario>();
        try{
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_ListarHorarios()}");
            ResultSet resultado = procedimiento.executeQuery();
            while(resultado.next()){
                lista.add(new Horario(resultado.getInt("codigoHorario"),
                    resultado.getString("horarioEntrada"),
                    resultado.getString("horarioSalida"),
                    resultado.getBoolean("lunes"),
                    resultado.getBoolean("martes"),
                    resultado.getBoolean("miercoles"),
                    resultado.getBoolean("jueves"),
                    resultado.getBoolean("viernes")));
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return listaHorarios = FXCollections.observableList(lista);
    }
    
    public void seleccionarElemento(){
        if(tblHorarios.getSelectionModel().isEmpty()){
            tblHorarios.getSelectionModel();
        }else{
            txtHoraInicio.setText(String.valueOf(((Horario)tblHorarios.getSelectionModel().getSelectedItem()).getHorarioEntrada()));
            txtHoraSalida.setText(String.valueOf(((Horario)tblHorarios.getSelectionModel().getSelectedItem()).getHorarioSalida()));
            cbLunes.setSelected(((Horario)tblHorarios.getSelectionModel().getSelectedItem()).isLunes());
            cbMartes.setSelected(((Horario)tblHorarios.getSelectionModel().getSelectedItem()).isMartes());
            cbMiercoles.setSelected(((Horario)tblHorarios.getSelectionModel().getSelectedItem()).isMiercoles());
            cbJueves.setSelected(((Horario)tblHorarios.getSelectionModel().getSelectedItem()).isJueves());
            cbViernes.setSelected(((Horario)tblHorarios.getSelectionModel().getSelectedItem()).isViernes());
        }
    }
    
    public Horario buscarHorario(int codigoHorario){
        Horario resultado = null;
        try{
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_BuscarHorario(?)}");
            procedimiento.setInt(1, codigoHorario);
            ResultSet registro = procedimiento.executeQuery();
            while(registro.next()){
                resultado = new Horario(registro.getInt("codigoHorario"),
                    registro.getString("horarioEntrada"),
                    registro.getString("horarioSalida"),
                    registro.getBoolean("lunes"),
                    registro.getBoolean("martes"),
                    registro.getBoolean("miercoles"),
                    registro.getBoolean("jueves"),
                    registro.getBoolean("viernes"));
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
                tblHorarios.setDisable(false);
                btnNuevo.setText("Nuevo");
                btnEliminar.setText("Eliminar");
                btnEditar.setDisable(false);
                btnReporte.setDisable(false);
                tipoDeOperacion = operaciones.NINGUNO;
                limpiarControles();
                if(tblHorarios.getSelectionModel().getSelectedItem() != null){
                    seleccionarElemento();
                }
                break;
            default:
                seleccionarElemento();
                if(tblHorarios.getSelectionModel().getSelectedItem() != null){
                    int respuesta = JOptionPane.showConfirmDialog(null, "¿Está seguro de que desea eliminar el registro?", "Eliminar Horario", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                    if(respuesta == JOptionPane.YES_OPTION){
                        try{
                            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_EliminarHorario(?)}");
                            procedimiento.setInt(1, ((Horario) tblHorarios.getSelectionModel().getSelectedItem()).getCodigoHorario());
                            procedimiento.execute();
                            listaHorarios.remove(tblHorarios.getSelectionModel().getSelectedIndex());
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
                tblHorarios.setDisable(true);
                btnNuevo.setText("Guardar");
                btnEliminar.setText("Cancelar");
                btnEditar.setDisable(true);
                btnReporte.setDisable(true);
                tipoDeOperacion = operaciones.GUARDAR;
                break;
            case GUARDAR:
                if(txtHoraInicio.getText().equals("") || txtHoraSalida.getText().equals("")){
                    JOptionPane.showMessageDialog(null, "Debe llenar todos los campos.");
                }else if(txtHoraInicio.getText().length()!=8){
                    JOptionPane.showMessageDialog(null, "La hora ingresada no es válida \nEl formato correcto es HH:MM:SS");
                    txtHoraInicio.setText("");
                }else if(txtHoraSalida.getText().length()!=8){
                    JOptionPane.showMessageDialog(null, "La hora ingresada no es válida \nEl formato correcto es HH:MM:SS");
                    txtHoraSalida.setText("");
                }else if(!(txtHoraInicio.getText().substring(2,3)).equals(":") && !(txtHoraInicio.getText().substring(5,6)).equals(":")){
                    JOptionPane.showMessageDialog(null, "La hora ingresada no es válida \nEl formato correcto es HH:MM:SS");
                    txtHoraInicio.setText("");
                }else if(!(txtHoraSalida.getText().substring(2,3)).equals(":") && !(txtHoraSalida.getText().substring(5,6)).equals(":")){
                    JOptionPane.showMessageDialog(null, "La hora ingresada no es válida \nEl formato correcto es HH:MM:SS");
                    txtHoraSalida.setText("");
                }else if((Integer.parseInt(txtHoraInicio.getText().substring(0,2))>24) || (Integer.parseInt(txtHoraInicio.getText().substring(3,5))>=60)
                        || (Integer.parseInt(txtHoraInicio.getText().substring(6,8))>=60)){
                    JOptionPane.showMessageDialog(null, "La hora ingresada no es válida");
                    txtHoraInicio.setText("");
                }else if((Integer.parseInt(txtHoraSalida.getText().substring(0,2))>24) || (Integer.parseInt(txtHoraSalida.getText().substring(3,5))>=60)
                        || (Integer.parseInt(txtHoraSalida.getText().substring(6,8))>=60)){
                    JOptionPane.showMessageDialog(null, "La hora ingresada no es válida");
                    txtHoraSalida.setText("");
                }else{
                    boolean validarHoraInicio = validacionHoras(txtHoraInicio.getText().substring(0, 2), txtHoraInicio.getText().substring(3, 5), 
                        txtHoraInicio.getText().substring(6, 8));
                    boolean validarHoraSalida = validacionHoras(txtHoraSalida.getText().substring(0, 2), txtHoraSalida.getText().substring(3, 5), 
                        txtHoraSalida.getText().substring(6, 8));
                    if(validarHoraInicio == false){
                    JOptionPane.showMessageDialog(null, "La hora ingresada no es válida.");
                    txtHoraInicio.setText("");
                    }else if(validarHoraSalida == false){
                    JOptionPane.showMessageDialog(null, "La hora ingresada no es válida.");
                    txtHoraSalida.setText("");
                    }else{
                    guardar();
                    desactivarControles();
                    tblHorarios.setDisable(false);
                    btnNuevo.setText("Nuevo");
                    btnEliminar.setText("Eliminar");
                    btnEditar.setDisable(false);
                    btnReporte.setDisable(false);
                    tipoDeOperacion = operaciones.NINGUNO;
                    limpiarControles();
                    cargarDatos();}
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
                if(tblHorarios.getSelectionModel().getSelectedItem() != null){
                    seleccionarElemento();
                }
                break;
            /*case NINGUNO:
                imprimirReporte();
                limpiarControles();
                tipoDeOperacion = operaciones.NINGUNO;
                if(tblHorarios.getSelectionModel().getSelectedItem() != null){
                    seleccionarElemento();
                break;*/
                }
        }
    
    public void guardar(){
        Horario registro = new Horario();
        registro.setHorarioEntrada(txtHoraInicio.getText());
        registro.setHorarioSalida(txtHoraSalida.getText());
        registro.setLunes(cbLunes.isSelected());
        registro.setMartes(cbMartes.isSelected());
        registro.setMiercoles(cbMiercoles.isSelected());
        registro.setJueves(cbJueves.isSelected());
        registro.setViernes(cbViernes.isSelected());
        try{
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_AgregarHorario(?,?,?,?,?,?,?)}");
            procedimiento.setString(1, registro.getHorarioEntrada());
            procedimiento.setString(2, registro.getHorarioSalida());
            procedimiento.setBoolean(3, registro.isLunes());
            procedimiento.setBoolean(4, registro.isMartes());
            procedimiento.setBoolean(5, registro.isMiercoles());
            procedimiento.setBoolean(6, registro.isJueves());
            procedimiento.setBoolean(7, registro.isViernes());
            procedimiento.execute();
            listaHorarios.add(registro);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void editar(){
        switch(tipoDeOperacion){
            case NINGUNO:
                if(tblHorarios.getSelectionModel().getSelectedItem() != null){
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
                if(txtHoraInicio.getText().equals("") || txtHoraSalida.getText().equals("")){
                    JOptionPane.showMessageDialog(null, "Debe llenar todos los campos.");
                }else if(txtHoraInicio.getText().length()!=8){
                    JOptionPane.showMessageDialog(null, "La hora ingresada no es válida \nEl formato correcto es HH:MM:SS");
                    txtHoraInicio.setText("");
                }else if(txtHoraSalida.getText().length()!=8){
                    JOptionPane.showMessageDialog(null, "La hora ingresada no es válida \nEl formato correcto es HH:MM:SS");
                    txtHoraSalida.setText("");
                }else if(!(txtHoraInicio.getText().substring(2,3)).equals(":") && !(txtHoraInicio.getText().substring(5,6)).equals(":")){
                    JOptionPane.showMessageDialog(null, "La hora ingresada no es válida \nEl formato correcto es HH:MM:SS");
                    txtHoraInicio.setText("");
                }else if(!(txtHoraSalida.getText().substring(2,3)).equals(":") && !(txtHoraSalida.getText().substring(5,6)).equals(":")){
                    JOptionPane.showMessageDialog(null, "La hora ingresada no es válida \nEl formato correcto es HH:MM:SS");
                    txtHoraSalida.setText("");
                }else if((Integer.parseInt(txtHoraInicio.getText().substring(0,2))>24) || (Integer.parseInt(txtHoraInicio.getText().substring(3,5))>=60)
                        || (Integer.parseInt(txtHoraInicio.getText().substring(6,8))>=60)){
                    JOptionPane.showMessageDialog(null, "La hora ingresada no es válida");
                    txtHoraInicio.setText("");
                }else if((Integer.parseInt(txtHoraSalida.getText().substring(0,2))>24) || (Integer.parseInt(txtHoraSalida.getText().substring(3,5))>=60)
                        || (Integer.parseInt(txtHoraSalida.getText().substring(6,8))>=60)){
                    JOptionPane.showMessageDialog(null, "La hora ingresada no es válida");
                    txtHoraSalida.setText("");
                }else{
                    boolean validarHoraInicio = validacionHoras(txtHoraInicio.getText().substring(0, 2), txtHoraInicio.getText().substring(3, 5), 
                        txtHoraInicio.getText().substring(6, 8));
                    boolean validarHoraSalida = validacionHoras(txtHoraSalida.getText().substring(0, 2), txtHoraSalida.getText().substring(3, 5), 
                        txtHoraSalida.getText().substring(6, 8));
                    if(validarHoraInicio == false){
                    JOptionPane.showMessageDialog(null, "La hora ingresada no es válida.");
                    txtHoraInicio.setText("");
                    }else if(validarHoraSalida == false){
                    JOptionPane.showMessageDialog(null, "La hora ingresada no es válida.");
                    txtHoraSalida.setText("");
                    }else{
                    actualizar();
                    desactivarControles();
                    btnEditar.setText("Editar");
                    btnReporte.setText("Reporte");
                    btnNuevo.setDisable(false);
                    btnEliminar.setDisable(false);
                    tipoDeOperacion = operaciones.NINGUNO;
                    limpiarControles();
                    cargarDatos();}
                }break;
        }
    }
    
    public void actualizar(){
        try{
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_EditarHorario(?,?,?,?,?,?,?,?)}");
            Horario registro = (Horario)tblHorarios.getSelectionModel().getSelectedItem();
            registro.setHorarioEntrada(txtHoraInicio.getText());
            registro.setHorarioSalida(txtHoraSalida.getText());
            registro.setLunes(cbLunes.isSelected());
            registro.setMartes(cbMartes.isSelected());
            registro.setMiercoles(cbMiercoles.isSelected());
            registro.setJueves(cbJueves.isSelected());
            registro.setViernes(cbViernes.isSelected());
            procedimiento.setInt(1, registro.getCodigoHorario());
            procedimiento.setString(2, registro.getHorarioEntrada());
            procedimiento.setString(3, registro.getHorarioSalida());
            procedimiento.setBoolean(4, registro.isLunes());
            procedimiento.setBoolean(5, registro.isMartes());
            procedimiento.setBoolean(6, registro.isMiercoles());
            procedimiento.setBoolean(7, registro.isJueves());
            procedimiento.setBoolean(8, registro.isViernes());
            procedimiento.execute();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    /*public void imprimirReporte(){
        Map parametros = new HashMap();
        parametros.put("codigoHorario", null);
        GenerarReporte.mostrarReporte("ReporteHorarios.jasper", "Reporte de Horarios", parametros);
    }*/
    
    public boolean validacionHoras(String hora, String minuto, String segundo){
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
        txtHoraSalida.setEditable(false);
        cbLunes.setDisable(true);
        cbMartes.setDisable(true);
        cbMiercoles.setDisable(true);
        cbJueves.setDisable(true);
        cbViernes.setDisable(true);
    }
    
    public void activarControles(){
        txtHoraInicio.setEditable(true);
        txtHoraSalida.setEditable(true);
        cbLunes.setDisable(false);
        cbMartes.setDisable(false);
        cbMiercoles.setDisable(false);
        cbJueves.setDisable(false);
        cbViernes.setDisable(false);
    }
    
    public void limpiarControles(){
        switch(tipoDeOperacion){
            case NINGUNO:
                txtHoraInicio.setText("");
                txtHoraSalida.setText("");
                tblHorarios.getSelectionModel().clearSelection();
                cbLunes.setSelected(false);
                cbMartes.setSelected(false);
                cbMiercoles.setSelected(false);
                cbJueves.setSelected(false);
                cbViernes.setSelected(false);
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
    
    public void ventanaMedicos(){
        escenarioPrincipal.ventanaMedicos();
    }
}
