/*
	Programador:
		Erick Stiv Junior Guerra Muñoz
	Creación:
		24-05-2019
	Modificaciones:
		Creación de procedimientos almacenados y triggers: 30-06-2019
        
*/

DROP DATABASE IF EXISTS DBHospitalInfectologia2018037;
CREATE DATABASE DBHospitalInfectologia2018037;

USE DBHospitalInfectologia2018037;

ALTER USER 'root'@'localhost' IDENTIFIED WITH mysql_native_password BY 'admin';

-- ++++++++++++++++++++++++++ CREACIÓN DE LAS TABLAS ++++++++++++++++++++++++++ --

CREATE TABLE Pacientes(
	codigoPaciente INT NOT NULL AUTO_INCREMENT,
    DPI VARCHAR(20) NOT NULL,
    apellidos VARCHAR(100) NOT NULL,
    nombres VARCHAR(100) NOT NULL,
    fechaNacimiento DATE NOT NULL,
    edad INT,
    direccion VARCHAR(150) NOT NULL,
    ocupacion VARCHAR(50) NOT NULL,
    sexo VARCHAR(15) NOT NULL,
    PRIMARY KEY PK_codigoPaciente(codigoPaciente)
);

CREATE TABLE ContactoUrgencia(
	codigoContactoUrgencia INT NOT NULL AUTO_INCREMENT,
    nombres VARCHAR(100) NOT NULL,
    apellidos VARCHAR(100) NOT NULL,
    numeroContacto VARCHAR(10) NOT NULL,
    codigoPaciente INT NOT NULL UNIQUE,
    PRIMARY KEY PK_codigoContactoUrgencia(codigoContactoUrgencia),
	CONSTRAINT FK_ContactoUrgencia_Pacientes FOREIGN KEY(codigoPaciente)
		REFERENCES Pacientes(codigoPaciente) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE Areas(
	codigoArea INT NOT NULL AUTO_INCREMENT,
    nombreArea VARCHAR(45) NOT NULL,
	PRIMARY KEY PK_codigoArea(codigoArea)
);

CREATE TABLE Cargos(
	codigoCargo INT NOT NULL AUTO_INCREMENT,
    nombreCargo VARCHAR(45) NOT NULL,
    PRIMARY KEY PK_codigoCargo(codigoCargo)
);

CREATE TABLE Medicos(
	codigoMedico INT NOT NULL AUTO_INCREMENT,
    licenciaMedica BIGINT NOT NULL,
    nombres VARCHAR(100) NOT NULL,
    apellidos VARCHAR(100) NOT NULL,
    horaEntrada VARCHAR(10) NOT NULL,
    horaSalida VARCHAR(10) NOT NULL,
    turnoMaximo INT NOT NULL DEFAULT 0,
    sexo VARCHAR(20),
    PRIMARY KEY PK_codigoMedico(codigoMedico)
);

CREATE TABLE Horarios(
	codigoHorario INT NOT NULL AUTO_INCREMENT,
    horarioEntrada VARCHAR(10) NOT NULL,
    horarioSalida VARCHAR(10) NOT NULL,
    lunes BOOLEAN,
    martes BOOLEAN,
    miercoles BOOLEAN,
    jueves BOOLEAN,
    viernes BOOLEAN,
    PRIMARY KEY PK_codigoHorario(codigoHorario)
);

CREATE TABLE TelefonoMedicos(
	codigoTelefonoMedico INT NOT NULL AUTO_INCREMENT,
    telefonoPersonal VARCHAR(15) NOT NULL,
    telefonoTrabajo VARCHAR(51),
    codigoMedico INT NOT NULL UNIQUE,
    PRIMARY KEY PK_codigoTelefonoMedico(codigoTelefonoMedico),
    CONSTRAINT FK_TelefonoMedicos_Medicos FOREIGN KEY (codigoMedico)
		REFERENCES Medicos(codigoMedico) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE ResponsableTurno(
	codigoResponsableTurno INT NOT NULL AUTO_INCREMENT,
    nombreResponsable VARCHAR(75) NOT NULL,
    apellidosResponsable VARCHAR(45) NOT NULL,
    telefonoPersonal VARCHAR(10) NOT NULL,
    codigoArea INT NOT NULL,
    codigoCargo INT NOT NULL,
    PRIMARY KEY PK_codigoResponsableTurno(codigoResponsableTurno),
    CONSTRAINT FK_ResponsableTurno_Areas FOREIGN KEY (codigoArea)
		REFERENCES Areas (codigoArea) ON DELETE CASCADE ON UPDATE CASCADE,
	CONSTRAINT FK_ResponsableTurno_Cargos FOREIGN KEY (codigoCargo)
		REFERENCES Cargos(codigoCargo) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE Especialidades(
	codigoEspecialidad INT NOT NULL AUTO_INCREMENT,
	nombreEspecialidad VARCHAR(45) NOT NULL,
    PRIMARY KEY PK_codigoEspecialidad(codigoEspecialidad)
);

CREATE TABLE MedicoEspecialidad(
	codigoMedicoEspecialidad INT NOT NULL AUTO_INCREMENT,
    codigoMedico INT NOT NULL,
    codigoEspecialidad INT NOT NULL,
    codigoHorario INT NOT NULL,
    PRIMARY KEY PK_codigoMedicoEspecialidad(codigoMedicoEspecialidad),
    CONSTRAINT FK_MedicoEspecialidad_Especialidades FOREIGN KEY(codigoEspecialidad)
		REFERENCES Especialidades (codigoEspecialidad) ON DELETE CASCADE ON UPDATE CASCADE,
	CONSTRAINT FK_MedicoEspecialidad_Medicos FOREIGN KEY (codigoMedico)
		REFERENCES Medicos(codigoMedico) ON DELETE CASCADE ON UPDATE CASCADE,
	CONSTRAINT FK_MedicoEspecialidad_Horario FOREIGN KEY(codigoHorario)
		REFERENCES Horarios(codigoHorario) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE Turno(
	codigoTurno INT NOT NULL AUTO_INCREMENT,
    fechaTurno DATE NOT NULL,
    fechaCita DATE NOT NULL,
    valorCita DECIMAL(10,2),
    codigoMedicoEspecialidad INT NOT NULL,
    codigoResponsableTurno INT NOT NULL,
    codigoPaciente INT NOT NULL,
    PRIMARY KEY PK_codigoTurno(codigoTurno),
    CONSTRAINT FK_Turno_ResponsableTurno FOREIGN KEY (codigoResponsableTurno)
		REFERENCES ResponsableTurno (codigoResponsableTurno) ON DELETE CASCADE ON UPDATE CASCADE,
	CONSTRAINT FK_Turno_Pacientes FOREIGN KEY (codigoPaciente)
		REFERENCES Pacientes (codigoPaciente) ON DELETE CASCADE ON UPDATE CASCADE,
	CONSTRAINT FK_Turno_MedicoEspecialidad FOREIGN KEY(codigoMedicoEspecialidad)
		REFERENCES MedicoEspecialidad(codigoMedicoEspecialidad) ON DELETE CASCADE ON UPDATE CASCADE
);

--+++++++++++++++++++++++++++++++++++ TRIGGERS ++++++++++++++++++++++++++++++++++--
DELIMITER $$
CREATE TRIGGER tr_Pacientes_Before_Insert
	Before INSERT ON dbhospitalinfectologia2018037.Pacientes
    FOR EACH ROW
    BEGIN
			Set new.edad = timestampdiff(YEAR, NEW.fechaNacimiento, now());
    END$$
DELIMITER ;

DELIMITER $$
CREATE TRIGGER tr_Pacientes_Before_Update
	BEFORE UPDATE ON DBHospitalInfectologia2018037.Pacientes
    FOR EACH ROW
    BEGIN
		Set new.edad = timestampdiff(YEAR, NEW.fechaNacimiento, now());
	END$$
DELIMITER ;

-- ++++++++++++++++++++ Procedimientos almacenados PACIENTES ++++++++++++++++++++ --
DELIMITER $$
CREATE PROCEDURE sp_AgregarPaciente (
	IN DPI VARCHAR(20), 
    IN apellidos VARCHAR(100), 
    IN nombres VARCHAR(100), 
    IN fechaNacimiento DATE, 
	IN direccion VARCHAR(150), 
    IN ocupacion VARCHAR(50), 
    IN sexo VARCHAR(15)
)
    BEGIN
		INSERT INTO Pacientes (DPI, apellidos, nombres, fechaNacimiento, direccion, ocupacion, sexo)
			VALUES (DPI, apellidos, nombres, fechaNacimiento, direccion, ocupacion, sexo);
	END$$
DELIMITER ;

DELIMITER $$
CREATE PROCEDURE sp_EliminarPaciente (
	IN codigoP INT
)
    BEGIN
		DELETE FROM Pacientes
			WHERE codigoPaciente = codigoP;
	END$$
DELIMITER ;

DELIMITER $$
CREATE PROCEDURE sp_ListarPacientes ()
    BEGIN
		SELECT
			Pacientes.codigoPaciente, 
            Pacientes.DPI, 
            Pacientes.apellidos, 
            Pacientes.nombres, 
            Pacientes.fechaNacimiento, 
            Pacientes.edad, 
            Pacientes.direccion, 
            Pacientes.ocupacion, 
            Pacientes.sexo
            FROM Pacientes;
	END$$
DELIMITER ;

DELIMITER $$
CREATE PROCEDURE sp_EditarPaciente (
	IN codigoP INT, 
    IN DPIpaciente VARCHAR(20), 
	IN apellidosPaciente VARCHAR(100), 
    IN nombresPaciente VARCHAR(100), 
	IN fechaNacimientoPaciente DATE, 
    IN edadPaciente INT, 
    IN direccionPaciente VARCHAR(150), 
    IN ocupacionPaciente VARCHAR(50), 
    IN sexoPaciente VARCHAR(15)
)
    BEGIN
		UPDATE Pacientes SET
			DPI = DPIpaciente, apellidos = apellidosPaciente, nombres = nombresPaciente, fechaNacimiento = fechaNacimientoPaciente, edad = edadPaciente, 
				direccion = direccionPaciente, ocupacion = ocupacionPaciente, sexo = sexoPaciente
            WHERE codigoPaciente = codigoP;
	END$$
DELIMITER ;

DELIMITER $$
CREATE PROCEDURE sp_BuscarPaciente (
	IN codigoP INT
)
	BEGIN
		SELECT
			Pacientes.codigoPaciente, 
            Pacientes.DPI, 
            Pacientes.apellidos, 
            Pacientes.nombres, 
            Pacientes.fechaNacimiento, 
            Pacientes.edad, 
            Pacientes.direccion, 
            Pacientes.ocupacion, 
            Pacientes.sexo
            FROM Pacientes
            WHERE codigoPaciente = codigoP;
	END$$
DELIMITER ;

-- ++++++++++++++++++++ Procedimientos almacenados CONTACTOURGENCIA ++++++++++++++++++++ --
DELIMITER $$
CREATE PROCEDURE sp_AgregarContacto (
	IN nombres VARCHAR(100), 
    IN apellidos VARCHAR(100), 
    IN numeroContacto VARCHAR(10), 
    IN codigoPaciente INT
)
    BEGIN
		INSERT INTO ContactoUrgencia (nombres, apellidos, numeroContacto, codigoPaciente)
			VALUES (nombres, apellidos, numeroContacto, codigoPaciente);
	END$$
DELIMITER ;

DELIMITER $$
CREATE PROCEDURE sp_EliminarContacto (
	IN codigoCU INT
)
    BEGIN
		DELETE FROM ContactoUrgencia
			WHERE codigoContactoUrgencia = codigoCU;
	END$$
DELIMITER ;

DELIMITER $$
CREATE PROCEDURE sp_ListarContactos ()
    BEGIN
		SELECT
			ContactoUrgencia.codigoContactoUrgencia, 
            ContactoUrgencia.nombres, 
            ContactoUrgencia.apellidos, 
            ContactoUrgencia.numeroContacto, 
            ContactoUrgencia.codigoPaciente
            FROM ContactoUrgencia;
	END$$
DELIMITER ;

DELIMITER $$
CREATE PROCEDURE sp_EditarContacto (
	IN codigoCU INT, 
    IN nombresContacto VARCHAR(100), 
    IN apellidosContacto VARCHAR(100), 
	IN numeroContactoUrgencia VARCHAR(10)
)
    BEGIN
		UPDATE ContactoUrgencia SET
			nombres = nombresContacto, apellidos = apellidosContacto, numeroContacto = numeroContactoUrgencia
            WHERE codigoContactoUrgencia = codigoCU;
	END$$
DELIMITER ;

DELIMITER $$
CREATE PROCEDURE sp_BuscarContacto (
	IN codigoCU INT
)
	BEGIN
		SELECT
			ContactoUrgencia.codigoContactoUrgencia, 
            ContactoUrgencia.nombres, 
            ContactoUrgencia.apellidos, 
            ContactoUrgencia.numeroContacto, 
            ContactoUrgencia.codigoPaciente
            FROM ContactoUrgencia
            WHERE codigoContactoUrgencia = codigoCU;
	END$$
DELIMITER ;

-- ++++++++++++++++++++ Procedimientos almacenados AREAS ++++++++++++++++++++ --
DELIMITER $$
CREATE PROCEDURE sp_AgregarArea (
	IN nombreArea VARCHAR(45)
)
    BEGIN
		INSERT INTO Areas (nombreArea)
			VALUES (nombreArea);
	END$$
DELIMITER ;

DELIMITER $$
CREATE PROCEDURE sp_EliminarArea (
	IN codigoA INT
)
    BEGIN
		DELETE FROM Areas
			WHERE codigoArea = codigoA;
	END$$
DELIMITER ;

DELIMITER $$
CREATE PROCEDURE sp_ListarAreas ()
    BEGIN
		SELECT
			Areas.codigoArea, 
            Areas.nombreArea
            FROM Areas;
	END$$
DELIMITER ;

DELIMITER $$
CREATE PROCEDURE sp_EditarArea (
	IN codigoA INT, 
	IN nombreAreaNueva VARCHAR(100)
)
    BEGIN
		UPDATE Areas SET
			nombreArea = nombreAreaNueva
            WHERE codigoArea = codigoA;
	END$$
DELIMITER ;

DELIMITER $$
CREATE PROCEDURE sp_BuscarArea (
	IN codigoA INT
)
	BEGIN
		SELECT
			Areas.codigoArea, 
            Areas.nombreArea
            FROM Areas
            WHERE codigoArea = codigoA;
	END$$
DELIMITER ;

-- ++++++++++++++++++++ Procedimientos almacenados CARGOS ++++++++++++++++++++ --
DELIMITER $$
CREATE PROCEDURE sp_AgregarCargo (
	IN nombreCargo VARCHAR(45)
)
    BEGIN
		INSERT INTO Cargos (nombreCargo)
			VALUES (nombreCargo);
	END$$
DELIMITER ;

DELIMITER $$
CREATE PROCEDURE sp_EliminarCargo (
	IN codigoC INT
)
    BEGIN
		DELETE FROM Cargos
			WHERE codigoCargo = codigoC;
	END$$
DELIMITER ;

DELIMITER $$
CREATE PROCEDURE sp_ListarCargos ()
    BEGIN
		SELECT
			Cargos.codigoCargo, 
            Cargos.nombreCargo
            FROM Cargos;
	END$$
DELIMITER ;

DELIMITER $$
CREATE PROCEDURE sp_EditarCargo (
	IN codigoC INT, 
    IN nombreCargoNuevo VARCHAR(100)
)
    BEGIN
		UPDATE Cargos SET
			nombreCargo = nombreCargoNuevo
            WHERE codigoCargo = codigoC;
	END$$
DELIMITER ;

DELIMITER $$
CREATE PROCEDURE sp_BuscarCargo (
	IN codigoC INT
)
	BEGIN
		SELECT
			Cargos.codigoCargo, 
            Cargos.nombreCargo
            FROM Cargos
            WHERE codigoCargo = codigoC;
	END$$
DELIMITER ;

-- ++++++++++++++++++++ Procedimientos almacenados MEDICOS ++++++++++++++++++++ --
DELIMITER $$
CREATE PROCEDURE sp_AgregarMedico (
	IN licenciaMedica BIGINT, 
    IN nombres VARCHAR(100), 
    IN apellidos VARCHAR(100), 
	IN horaEntrada VARCHAR(10), 
    IN horaSalida VARCHAR(10),
    IN sexo VARCHAR(20)
)
    BEGIN
		INSERT INTO Medicos (licenciaMedica, nombres, apellidos, horaEntrada, horaSalida, sexo)
			VALUES (licenciaMedica, nombres, apellidos, horaEntrada, horaSalida, sexo);
	END$$
DELIMITER ;

DELIMITER $$
CREATE PROCEDURE sp_EliminarMedico (
	IN codigoM INT
)
    BEGIN
		DELETE FROM Medicos
			WHERE codigoMedico = codigoM;
	END$$
DELIMITER ;

DELIMITER $$
CREATE PROCEDURE sp_ListarMedicos ()
    BEGIN
		SELECT
			Medicos.codigoMedico, 
            Medicos.licenciaMedica, 
            Medicos.nombres, 
            Medicos.apellidos, 
            Medicos.horaEntrada, 
            Medicos.horaSalida, 
            Medicos.turnoMaximo, 
            Medicos.sexo
            FROM Medicos;
	END$$
DELIMITER ;

DELIMITER $$
CREATE PROCEDURE sp_EditarMedico (
	IN codigoM INT, 
    IN licenciaMedicaMedico BIGINT, 
    IN nombresMedico VARCHAR(100), 
    IN apellidosMedico VARCHAR(100), 
	IN horaEntradaMedico VARCHAR(10), 
    IN horaSalidaMedico VARCHAR(10),
    IN sexoMedico VARCHAR(20)
)
    BEGIN
		UPDATE Medicos SET
			licenciaMedica = licenciaMedicaMedico, nombres = nombresMedico, apellidos = apellidosMedico, horaEntrada = horaEntradaMedico, 
				horaSalida = horaSalidaMedico, sexo = sexoMedico
            WHERE codigoMedico = codigoM;
	END$$
DELIMITER ;

DELIMITER $$
CREATE PROCEDURE sp_BuscarMedico (IN codigoM INT)
	BEGIN
		SELECT
			Medicos.codigoMedico, 
            Medicos.licenciaMedica, 
            Medicos.nombres, 
            Medicos.apellidos, 
            Medicos.horaEntrada, 
            Medicos.horaSalida, 
            Medicos.turnoMaximo, 
            Medicos.sexo
            FROM Medicos
            WHERE codigoMedico = codigoM;
	END$$
DELIMITER ;

-- ++++++++++++++++++++ Procedimientos almacenados HORARIOS ++++++++++++++++++++ --
DELIMITER $$
CREATE PROCEDURE sp_AgregarHorario (
	IN horarioEntrada VARCHAR(10), 
    IN horarioSalida VARCHAR(10), 
    IN lunes BOOLEAN, 
	IN martes BOOLEAN, 
    IN miercoles BOOLEAN, 
    IN jueves BOOLEAN, 
    IN viernes BOOLEAN
)
    BEGIN
		INSERT INTO Horarios (horarioEntrada, horarioSalida, lunes, martes, miercoles, jueves, viernes)
			VALUES (horarioEntrada, horarioSalida, lunes, martes, miercoles, jueves, viernes);
	END$$
DELIMITER ;

DELIMITER $$
CREATE PROCEDURE sp_EliminarHorario (
	IN codigoH INT
)
    BEGIN
		DELETE FROM Horarios
			WHERE codigoHorario = codigoH;
	END$$
DELIMITER ;

DELIMITER $$
CREATE PROCEDURE sp_ListarHorarios ()
    BEGIN
		SELECT
			Horarios.codigoHorario, 
            Horarios.horarioEntrada, 
            Horarios.horarioSalida, 
            Horarios.lunes, 
            Horarios.martes, 
            Horarios.miercoles, 
            Horarios.jueves, 
            Horarios.viernes
            FROM Horarios;
	END$$
DELIMITER ;

DELIMITER $$
CREATE PROCEDURE sp_EditarHorario (
	IN codigoH INT, 
    IN horarioDeEntrada VARCHAR(10), 
    IN horarioDeSalida VARCHAR(10), 
    IN lunesHorario BOOLEAN, 
	IN martesHorario BOOLEAN, 
    IN miercolesHorario BOOLEAN, 
    IN juevesHorario BOOLEAN, 
    IN viernesHorario BOOLEAN
)
    BEGIN
		UPDATE Horarios SET
			horarioEntrada = horarioDeEntrada, horarioSalida = horarioDeSalida, lunes = lunesHorario, martes = martesHorario, 
				miercoles = miercolesHorario, jueves = juevesHorario, viernes = viernesHorario
            WHERE codigoHorario = codigoH;
	END$$
DELIMITER ;

DELIMITER $$
CREATE PROCEDURE sp_BuscarHorario (
	IN codigoH INT
)
	BEGIN
		SELECT
			Horarios.codigoHorario, 
            Horarios.horarioEntrada, 
            Horarios.horarioSalida, 
            Horarios.lunes, 
            Horarios.martes, 
            Horarios.miercoles, 
            Horarios.jueves, 
            Horarios.viernes
            FROM Horarios
            WHERE codigoHorario = codigoH;
	END$$
DELIMITER ;

-- ++++++++++++++++++++ Procedimientos almacenados TELEFONOS_MEDICO ++++++++++++++++++++ --
DELIMITER $$
CREATE PROCEDURE sp_AgregarTelefonoMedico (
	IN telefonoPersonal VARCHAR(15), 
    IN telefonoTrabajo VARCHAR(15), 
	IN codigoMedico INT
)
    BEGIN
		INSERT INTO TelefonoMedicos (telefonoPersonal, telefonoTrabajo, codigoMedico)
			VALUES (telefonoPersonal, telefonoTrabajo, codigoMedico);
	END$$
DELIMITER ;

DELIMITER $$
CREATE PROCEDURE sp_EliminarTelefonoMedico (
	IN codigoTM INT
)
    BEGIN
		DELETE FROM TelefonoMedicos
			WHERE codigoTelefonoMedico = codigoTM;
	END$$
DELIMITER ;

DELIMITER $$
CREATE PROCEDURE sp_ListarTelefonoMedicos ()
    BEGIN
		SELECT
			TelefonoMedicos.codigoTelefonoMedico, 
            TelefonoMedicos.telefonoPersonal, 
            TelefonoMedicos.telefonoTrabajo, 
            TelefonoMedicos.codigoMedico
            FROM TelefonoMedicos;
	END$$
DELIMITER ;

DELIMITER $$
CREATE PROCEDURE sp_EditarTelefonoMedico (
	IN codigoTM INT, 
    IN personal VARCHAR(15), 
    IN trabajo VARCHAR(15)
)
    BEGIN
		UPDATE TelefonoMedicos SET
			telefonoPersonal = Personal, telefonoTrabajo = trabajo
            WHERE codigoTelefonoMedico = codigoTM;
	END$$
DELIMITER ;

DELIMITER $$
CREATE PROCEDURE sp_BuscarTelefonoMedico (
	IN codigoTM INT
)
	BEGIN
		SELECT
			TelefonoMedicos.codigoTelefonoMedico, 
            TelefonoMedicos.telefonoPersonal, 
            TelefonoMedicos.telefonoTrabajo, 
            TelefonoMedicos.codigoPedico
            FROM TelefonoMedicos
            WHERE codigoTelefonoMedico = codigoTM;
	END$$
DELIMITER ;

-- ++++++++++++++++++++ Procedimientos almacenados RESPONSABLE_TURNO ++++++++++++++++++++ --
DELIMITER $$
CREATE PROCEDURE sp_AgregarResponsableTurno(
	IN nombreResponsable VARCHAR(75), 
    IN apellidosResponsable VARCHAR(45), 
    IN telefonoPersonal VARCHAR(10),
	IN codigoArea INT, 
    IN codigoCargo INT
)
    BEGIN
		INSERT INTO ResponsableTurno (nombreResponsable, apellidosResponsable, telefonoPersonal, codigoArea, codigoCargo)
			VALUES (nombreResponsable, apellidosResponsable, telefonoPersonal, codigoArea, codigoCargo);
	END$$
DELIMITER ;

DELIMITER $$
CREATE PROCEDURE sp_EliminarResponsableTurno (
	IN codigoRT INT
)
    BEGIN
		DELETE FROM ResponsableTurno
			WHERE codigoResponsableTurno = codigoRT;
	END$$
DELIMITER ;

DELIMITER $$
CREATE PROCEDURE sp_ListarResponsablesTurno ()
    BEGIN
		SELECT
			ResponsableTurno.codigoResponsableTurno, 
            ResponsableTurno.nombreResponsable, 
            ResponsableTurno.apellidosResponsable, 
            ResponsableTurno.telefonoPersonal,
            ResponsableTurno.codigoArea,
            ResponsableTurno.codigoCargo
            FROM ResponsableTurno;
	END$$
DELIMITER ;

DELIMITER $$
CREATE PROCEDURE sp_EditarResponsableTurno (
	IN codigoRT INT, 
    IN nombreR VARCHAR(75), 
    IN apellidosR VARCHAR(45), 
	IN tPersonal VARCHAR(10)
)
    BEGIN
		UPDATE ResponsableTurno SET
			nombreResponsable = nombreR, apellidosResponsable = apellidosR, telefonoPersonal = tPersonal
            WHERE codigoResponsableTurno = codigoRT;
	END$$
DELIMITER ;

DELIMITER $$
CREATE PROCEDURE sp_BuscarResponsableTurno (
	IN codigoRT INT
)
	BEGIN
		SELECT
			ResponsableTurno.codigoResponsableTurno, 
            ResponsableTurno.nombreResponsable, 
            ResponsableTurno.apellidosResponsable, 
            ResponsableTurno.telefonoPersona,
            ResponsableTurno.codigoArea,
            ResponsableTurno.codigoCargo
            FROM ResponsableTurno
            WHERE codigoResponsableTurno = codigoRT;
	END$$
DELIMITER ;

-- ++++++++++++++++++++ Procedimientos almacenados ESPECIALIDADES ++++++++++++++++++++ --
DELIMITER $$
CREATE PROCEDURE sp_AgregarEspecialidad (
	IN nombreEspecialidad VARCHAR(45)
)
    BEGIN
		INSERT INTO Especialidades (nombreEspecialidad)
			VALUES (nombreEspecialidad);
	END$$
DELIMITER ;

DELIMITER $$
CREATE PROCEDURE sp_EliminarEspecialidad (
	IN codigoE INT
)
    BEGIN
		DELETE FROM Especialidades
			WHERE codigoEspecialidad = codigoE;
	END$$
DELIMITER ;

DELIMITER $$
CREATE PROCEDURE sp_ListarEspecialidades ()
    BEGIN
		SELECT
			Especialidades.codigoEspecialidad, 
            Especialidades.nombreEspecialidad
            FROM Especialidades;
	END$$
DELIMITER ;

DELIMITER $$
CREATE PROCEDURE sp_EditarEspecialidad (
	IN codigoE INT, 
    IN nombreEspecialidadNueva VARCHAR(45)
)
    BEGIN
		UPDATE Especialidades SET
			nombreEspecialidad = nombreEspecialidadNueva
            WHERE codigoEspecialidad = codigoE;
	END$$
DELIMITER ;

DELIMITER $$
CREATE PROCEDURE sp_BuscarEspecialidad (
	IN codigoE INT
)
	BEGIN
		SELECT
			Especialidades.codigoEspecialidad, 
            Especialidades.nombreEspecialidad
            FROM Especialidades
            WHERE codigoEspecialidad = codigoE;
	END$$
DELIMITER ;

-- ++++++++++++++++++++ Procedimientos almacenados MEDICO_ESPECIALIDAD ++++++++++++++++++++ --
DELIMITER $$
CREATE PROCEDURE sp_AgregarMedicoEspecialidad (
	IN codigoMedico INT, 
    IN codigoEspecialidad INT, 
    IN codigoHorario INT
)
    BEGIN
		INSERT INTO MedicoEspecialidad (codigoMedico, codigoEspecialidad, codigoHorario)
			VALUES (codigoMedico, codigoEspecialidad, codigoHorario);
	END$$
DELIMITER ;

DELIMITER $$
CREATE PROCEDURE sp_EliminarMedicoEspecialidad (
	IN codigoME INT
)
    BEGIN
		DELETE FROM MedicoEspecialidad
			WHERE codigoMedicoEspecialidad = codigoME;
	END$$
DELIMITER ;

DELIMITER $$
CREATE PROCEDURE sp_ListarMedicoEspecialidad()
    BEGIN
		SELECT 
            MedicoEspecialidad.codigoMedicoEspecialidad, 
            MedicoEspecialidad.codigoMedico, 
            MedicoEspecialidad.codigoEspecialidad, 
            MedicoEspecialidad.codigoHorario
            FROM MedicoEspecialidad;
	END$$
DELIMITER ;

DELIMITER $$
CREATE PROCEDURE sp_EditarMedicoEspecialidad (
	IN codigoME INT, 
    IN codigoM INT, 
    IN codigoE INT, 
    IN codigoH INT
)
    BEGIN
		UPDATE MedicoEspecialidad SET
			codigMedico = codigoM, codigoEspecialidad = codigoE, codigoHorario = codigoH
            WHERE codigoMedicoEspecialidad = codigoME;
	END$$
DELIMITER ;

DELIMITER $$
CREATE PROCEDURE sp_BuscarMedicoEspecialidad (
	IN codigoME INT
)
	BEGIN
		SELECT
			MedicoEspecialidad.codigoMedicoEspecialidad, 
            MedicoEspecialidad.codigoMedico, 
            MedicoEspecialidad.codigoEspecialidad, 
            MedicoEspecialidad.codigoHorario
            FROM MedicoEspecialidad
            WHERE codigoMedicoEspecialidad = codigoME;
	END$$
DELIMITER ;

-- ++++++++++++++++++++ Procedimientos almacenados TURNO ++++++++++++++++++++ --
DELIMITER $$
CREATE PROCEDURE sp_AgregarTurno (
	IN fechaTurno DATE, 
    IN fechaCita DATE, 
    IN valorCita DECIMAL(10,2), 
    IN codigoMedicoEspecialidad INT, 
	IN codigoResponsableTurno INT, 
    IN codigoPaciente INT
)
    BEGIN
		INSERT INTO Turno (fechaTurno, fechaCita, valorCita, codigoMedicoEspecialidad, codigoResponsableTurno, codigoPaciente)
			VALUES (fechaTurno, fechaCita, valorCita, codigoMedicoEspecialidad, codigoResponsableTurno, codigoPaciente);
	END$$
DELIMITER ;

DELIMITER $$
CREATE PROCEDURE sp_EliminarTurno (
	IN codigoT INT
)
    BEGIN
		DELETE FROM Turno
			WHERE codigoTurno = codigoT;
	END$$
DELIMITER ;

DELIMITER $$
CREATE PROCEDURE sp_ListarTurnos ()
    BEGIN
		SELECT
			Turno.codigoTurno, 
            Turno.fechaTurno, 
            Turno.fechaCita, 
            Turno.valorCita, 
            Turno.codigoMedicoEspecialidad, 
            Turno.codigoResponsableTurno, 
            Turno.codigoPaciente
            FROM Turno;
	END$$
DELIMITER ;

DELIMITER $$
CREATE PROCEDURE sp_EditarTurno (
	IN codigoT INT, 
    IN fechaT DATE, 
    IN fechaC DATE, 
    IN valorC DECIMAL(10,2)
)
    BEGIN
		UPDATE Turno SET
			fechaTurno = fechaT, fechaCita = fechaC, valorCita = valorC, codigoMedicoEspecialidad = codigoME, codigoResponsableTurno = codigoRT, 
			codigoPaciente = codigoP
            WHERE codigoTurno = codigoT;
	END$$
DELIMITER ;

DELIMITER $$
CREATE PROCEDURE sp_BuscarTurno (
	IN codigoT INT
)
	BEGIN
		SELECT
            Turno.codigoTurno, 
            Turno.fechaTurno, 
            Turno.fechaCita, 
            Turno.valorCita, 
            Turno.codigoMedicoEspecialidad, 
            Turno.codigoResponsableTurno, 
            Turno.codigoPaciente
            FROM Turno
            WHERE codigoTurno = codigoT;
	END$$
DELIMITER ;

DROP TABLE IF EXISTS DBHospitalInfectologia2018037.ControlCitas;
CREATE TABLE DBHospitalInfectologia2018037.ControlCitas(
	codigoControlCita INT AUTO_INCREMENT NOT NULL,
    fecha DATE NOT NULL,
    horaInicio VARCHAR(10) NOT NULL,
    horaFin VARCHAR(10) NOT NULL,
    codigoMedico INT NOT NULL,
    codigoPaciente INT NOT NULL,
    PRIMARY KEY PK_codigoControlCita(codigoControlCita),
    CONSTRAINT FK_ControlCitas_Medicos FOREIGN KEY(codigoMedico)
		REFERENCES Medicos (codigoMedico) ON DELETE CASCADE ON UPDATE CASCADE,
	CONSTRAINT FK_ControlCitas_Pacientes FOREIGN KEY(codigoPaciente)
		REFERENCES Pacientes (codigoPaciente) ON DELETE CASCADE ON UPDATE CASCADE
);

DROP TABLE IF EXISTS DBHospitalInfectologia2018037.Recetas;

CREATE TABLE DBHospitalInfectologia2018037.Recetas(
	codigoReceta INT NOT NULL AUTO_INCREMENT,
    descripcionReceta VARCHAR(150) NOT NULL,
    codigoControlCita INT NOT NULL,
    PRIMARY KEY PK_codigoReceta (codigoReceta),
    CONSTRAINT FK_Recetas_ControlCita FOREIGN KEY(codigoControlCita)
		REFERENCES ControlCitas(codigoControlCita) ON DELETE CASCADE ON UPDATE CASCADE
);

-- +++++++++++++++++++++++ PROCEDIMIENTOS ALMACENADOS CONTROLCITAS ++++++++++++++++++++++++ --

DROP PROCEDURE IF EXISTS DBHospitalInfectologia2018037.sp_AgregarControlCita;

DELIMITER $$
CREATE PROCEDURE sp_AgregarControlCita(
	IN fecha DATE, 
    IN horaInicio VARCHAR(10), 
    IN horaFin VARCHAR(10), 
    IN codigoMedico INT, 
    IN codigoPaciente INT
    )
	BEGIN
		INSERT INTO DBHospitalInfectologia2018037.ControlCitas(fecha, horaInicio, horaFin, codigoMedico, codigoPaciente)
			VALUES (fecha, horaInicio, horaFin, codigoMedico, codigoPaciente);
	END$$
DELIMITER ;

DROP PROCEDURE IF EXISTS DBHospitalInfectologia2018037.sp_ListarControlCitas;

DELIMITER $$
	CREATE PROCEDURE sp_ListarControlCitas()
		BEGIN
			SELECT 
				ControlCitas.codigoControlCita,
                ControlCitas.fecha,
                ControlCitas.horaInicio,
                ControlCitas.horaFin,
                ControlCitas.codigoMedico,
                ControlCitas.codigoPaciente
                FROM DBHospitalInfectologia2018037.ControlCitas;
		END$$
DELIMITER ;

DROP PROCEDURE IF EXISTS DBHospitalInfectologia2018037.sp_EliminarControlCita;

DELIMITER $$
CREATE PROCEDURE sp_EliminarControlCita(IN codigoCC INT)
	BEGIN
		DELETE FROM DBHospitalInfectologia2018037.ControlCitas
			Where codigoControlCita = codigoCC;
	END$$
DELIMITER ;

DROP PROCEDURE IF EXISTS DBHospitalInfectologia2018037.sp_EditarControlCita;

DELIMITER $$
CREATE PROCEDURE sp_EditarControlCita(
	IN codigoCC INT, 
    IN fechaCC DATE,
    IN horaI VARCHAR(10),
    IN horaF VARCHAR(10)
    )
    BEGIN
		UPDATE DBHospitalInfectologia2018037.ControlCitas SET
			fecha = fechaCC, horaInicio = horaI, horaFin = horaF 
            WHERE codigoControlCita = codigoCC;
	END$$
DELIMITER ;

DROP PROCEDURE IF EXISTS DBHospitalInfectologia2018037.sp_BuscarControlCita;

DELIMITER $$
CREATE PROCEDURE sp_BuscarControlCita(IN codigoCC INT)
	BEGIN
		SELECT
			ControlCitas.codigoControlCita,
            ControlCitas.fecha,
            ControlCitas.horaInicio,
            ControlCitas.horaFin,
            ControlCitas.codigoMedico,
            ControlCitas.codigoPaciente
            FROM DBHospitalInfectologia2018037.ControlCitas 
            WHERE codigoControlCita = codigoCC;
	END$$
DELIMITER ;
        
-- +++++++++++++++++++++++++ PROCEDIMIENTOS ALMACENADOS RECETAS +++++++++++++++++++++++++++ --

DROP PROCEDURE IF EXISTS DBHospitalInfectologia2018037.sp_AgregarReceta;

DELIMITER $$
CREATE PROCEDURE sp_AgregarReceta(IN descripcionReceta VARCHAR(150), IN codigoControlCita INT)
	BEGIN
		INSERT INTO DBHospitalInfectologia2018037.Recetas(descripcionReceta, codigoControlCita)
			VALUES (descripcionReceta, codigoControlCita);
	END$$
DELIMITER ;

DROP PROCEDURE IF EXISTS DBHospitalInfectologia2018037.sp_ListarRecetas;

DELIMITER $$
CREATE PROCEDURE sp_ListarRecetas()
	BEGIN
		SELECT
			Recetas.codigoReceta,
            Recetas.descripcionReceta,
            Recetas.codigoControlCita
            FROM DBHospitalInfectologia2018037.Recetas;
	END$$
DELIMITER ;

DROP PROCEDURE IF EXISTS DBHospitalInfectologia2018037.sp_EliminarReceta;

DELIMITER $$
CREATE PROCEDURE sp_EliminarReceta(IN codigoR INT)
	BEGIN
		DELETE FROM DBHospitalInfectologia2018037.Recetas 
			Where codigoReceta = codigoR;
	END$$
DELIMITER ;

DROP PROCEDURE IF EXISTS DBHospitalInfectologia2018037.sp_EditarReceta;

DELIMITER $$
CREATE PROCEDURE sp_EditarReceta(
	IN codigoR INT, 
    IN descripcion VARCHAR(150)
    )
    BEGIN
		UPDATE DBHospitalInfectologia2018037.Recetas SET
			descripcionReceta = descripcion
            WHERE codigoReceta = codigoR;
	END$$
DELIMITER ;

DROP PROCEDURE IF EXISTS DBHospitalInfectologia2018037.sp_BuscarReceta;

DELIMITER $$
CREATE PROCEDURE sp_BuscarReceta(IN codigoR INT)
	BEGIN
		SELECT
			Recetas.codigoReceta,
            Recetas.descripcionReceta,
            Recetas.codigoControlCita
            FROM DBHospitalInfectologia2018037.Recetas
            WHERE codigoReceta = codigoR;
	END$$
DELIMITER ;

drop procedure if exists sp_ReporteCitas;

DELIMITER $$
	CREATE PROCEDURE sp_ReporteCitas(IN codigoP INT)
		BEGIN
			select C.codigoControlCita, C.horaInicio, C.horaFin, P.codigoPaciente, P.DPI, P.nombres as NombrePaciente, P.apellidos as ApellidosPaciente, P.edad, P.direccion,
            M.licenciaMedica, M.nombres as NombreMedico, M.apellidos as ApellidoMedico, R.descripcionReceta 
				FROM Medicos M INNER JOIN ControlCitas C ON C.codigoMedico=M.codigoMedico 
				INNER JOIN Pacientes P ON P.codigoPaciente = C.codigoPaciente 
				LEFT JOIN Recetas R ON R.codigoControlCita = C.codigoControlCita WHERE P.codigoPaciente = codigoP;
		END$$
DELIMITER ; 