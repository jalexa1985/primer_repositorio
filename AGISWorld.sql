/*
 * ESCUELA COLOMBIANA DE INGENIERIA
 * MODELOS Y SERVICIOS DE DATOS - 2026-2
 * Laboratorio 4/6
 *
 * AGISWorld. Planes de Formacion + Notificaciones
 * Motor: PostgreSQL
 *
 * Estandar de nombres de restricciones:
 *   PK_<TABLA>                 Llave primaria
 *   UK_<TABLA>_<ATRIBUTO>      Llave unica
 *   FK_<TABLA>_<TABLAREF>      Llave foranea
 *   CK_<TABLA>_<ATRIBUTO>      Restriccion de atributo
 *   CK_<TABLA>_<REGLA>         Restriccion de tupla
 *   TR_<TABLA>_<MOMENTO>       Disparador (BI, BU, BD, AI, AU)
 *   FN_<TABLA>_<MOMENTO>       Funcion asociada al disparador
 *
 * Orden de ejecucion: XDisparadores, XTablas (limpieza), Tablas, Atributos,
 * Tuplas, Acciones, Disparadores, TuplasOK, TuplasNoOK, AccionesOK,
 * DisparadoresOK, DisparadoresNoOK, Consultas.
 * Los casos NoOK DEBEN fallar; cada uno explica la regla que viola.
 */


/* ======================================================================== */
/* XDisparadores                                                            */
/* ======================================================================== */
DROP TRIGGER IF EXISTS TR_NOTIFICACIONES_BD ON notificaciones;
DROP TRIGGER IF EXISTS TR_NOTIFICACIONES_BU ON notificaciones;
DROP TRIGGER IF EXISTS TR_NOTIFICACIONES_BI ON notificaciones;
DROP TRIGGER IF EXISTS TR_INSCRIPCIONES_BI ON inscripciones;
DROP TRIGGER IF EXISTS TR_ACTIVIDADES_BD ON actividades;
DROP TRIGGER IF EXISTS TR_ACTIVIDADES_BU ON actividades;
DROP TRIGGER IF EXISTS TR_ACTIVIDADES_BI ON actividades;
DROP TRIGGER IF EXISTS TR_PLANESFORMACION_AU ON planesFormacion;
DROP TRIGGER IF EXISTS TR_PLANESFORMACION_BD ON planesFormacion;
DROP TRIGGER IF EXISTS TR_PLANESFORMACION_BU ON planesFormacion;
DROP TRIGGER IF EXISTS TR_PLANESFORMACION_BI ON planesFormacion;

DROP FUNCTION IF EXISTS FN_NOTIFICACIONES_BD();
DROP FUNCTION IF EXISTS FN_NOTIFICACIONES_BU();
DROP FUNCTION IF EXISTS FN_NOTIFICACIONES_BI();
DROP FUNCTION IF EXISTS FN_INSCRIPCIONES_BI();
DROP FUNCTION IF EXISTS FN_ACTIVIDADES_BD();
DROP FUNCTION IF EXISTS FN_ACTIVIDADES_BU();
DROP FUNCTION IF EXISTS FN_ACTIVIDADES_BI();
DROP FUNCTION IF EXISTS FN_PLANESFORMACION_AU();
DROP FUNCTION IF EXISTS FN_PLANESFORMACION_BD();
DROP FUNCTION IF EXISTS FN_PLANESFORMACION_BU();
DROP FUNCTION IF EXISTS FN_PLANESFORMACION_BI();


/* ======================================================================== */
/* XTablas                                                                  */
/* ======================================================================== */
DROP TABLE IF EXISTS notificaciones CASCADE;
DROP TABLE IF EXISTS inscripciones CASCADE;
DROP TABLE IF EXISTS actividades CASCADE;
DROP TABLE IF EXISTS planesFormacion CASCADE;
DROP TABLE IF EXISTS usuarios CASCADE;
DROP SEQUENCE IF EXISTS SEQ_NOTIFICACIONES;
DROP SEQUENCE IF EXISTS SEQ_PLANESFORMACION;


/* ======================================================================== */
/* Tablas                                                                   */
/* ======================================================================== */
/* Consecutivos: garantizan que un codigo o numero eliminado no se reutilice */
CREATE SEQUENCE SEQ_PLANESFORMACION START 1 MAXVALUE 9999;
CREATE SEQUENCE SEQ_NOTIFICACIONES START 1;

CREATE TABLE usuarios (
    idUsuario     INTEGER      NOT NULL,
    nombre        VARCHAR(50)  NOT NULL,
    correo        VARCHAR(100) NOT NULL,
    rol           CHAR(1)      NOT NULL,
    fechaRegistro DATE         NOT NULL DEFAULT CURRENT_DATE
);

CREATE TABLE planesFormacion (
    codigo        CHAR(6)      NOT NULL,
    nombre        VARCHAR(60)  NOT NULL,
    descripcion   VARCHAR(500),
    nivel         CHAR(1)      NOT NULL,
    fechaCreacion DATE         NOT NULL,
    fechaInicio   DATE,
    fechaFin      DATE,
    estado        CHAR(1)      NOT NULL,
    responsable   INTEGER      NOT NULL
);

CREATE TABLE actividades (
    plan          CHAR(6)      NOT NULL,
    numero        INTEGER      NOT NULL,
    nombre        VARCHAR(60)  NOT NULL,
    tipo          VARCHAR(10)  NOT NULL,
    horas         INTEGER      NOT NULL,
    obligatoria   BOOLEAN      NOT NULL DEFAULT TRUE
);

CREATE TABLE inscripciones (
    plan          CHAR(6)      NOT NULL,
    usuario       INTEGER      NOT NULL,
    fecha         DATE         NOT NULL,
    estado        CHAR(1)      NOT NULL
);

CREATE TABLE notificaciones (
    numero        INTEGER      NOT NULL,
    usuario       INTEGER      NOT NULL,
    plan          CHAR(6),
    fecha         TIMESTAMP    NOT NULL,
    tipo          VARCHAR(12)  NOT NULL,
    asunto        VARCHAR(80)  NOT NULL,
    mensaje       VARCHAR(500) NOT NULL,
    leida         BOOLEAN      NOT NULL,
    fechaLectura  TIMESTAMP
);


/* ======================================================================== */
/* Atributos                                                                */
/* ======================================================================== */
/* TId: entero positivo */
ALTER TABLE usuarios ADD CONSTRAINT CK_USUARIOS_IDUSUARIO
    CHECK (idUsuario > 0);
/* TCorreo: contiene @ y al menos un punto despues de la @ */
ALTER TABLE usuarios ADD CONSTRAINT CK_USUARIOS_CORREO
    CHECK (correo ~ '^[^@[:space:]]+@[^@[:space:]]+\.[^@[:space:]]+$');
/* TRol: (E)studiante, (P)rofesor, (A)dministrador */
ALTER TABLE usuarios ADD CONSTRAINT CK_USUARIOS_ROL
    CHECK (rol IN ('E', 'P', 'A'));

/* TCodigoPlan: PF seguido de 4 digitos */
ALTER TABLE planesFormacion ADD CONSTRAINT CK_PLANESFORMACION_CODIGO
    CHECK (codigo ~ '^PF[0-9]{4}$');
/* TNivel: (B)asico, (I)ntermedio, (A)vanzado */
ALTER TABLE planesFormacion ADD CONSTRAINT CK_PLANESFORMACION_NIVEL
    CHECK (nivel IN ('B', 'I', 'A'));
/* TEstadoPlan: (B)orrador, (A)ctivo, (F)inalizado, (C)ancelado */
ALTER TABLE planesFormacion ADD CONSTRAINT CK_PLANESFORMACION_ESTADO
    CHECK (estado IN ('B', 'A', 'F', 'C'));

/* TTipoActividad */
ALTER TABLE actividades ADD CONSTRAINT CK_ACTIVIDADES_TIPO
    CHECK (tipo IN ('CURSO', 'TALLER', 'PROYECTO', 'EVALUACION'));
/* THoras: entre 1 y 200 */
ALTER TABLE actividades ADD CONSTRAINT CK_ACTIVIDADES_HORAS
    CHECK (horas BETWEEN 1 AND 200);
ALTER TABLE actividades ADD CONSTRAINT CK_ACTIVIDADES_NUMERO
    CHECK (numero > 0);

/* TEstadoInscripcion: (I)nscrito, (R)etirado, (C)ompletado */
ALTER TABLE inscripciones ADD CONSTRAINT CK_INSCRIPCIONES_ESTADO
    CHECK (estado IN ('I', 'R', 'C'));

ALTER TABLE notificaciones ADD CONSTRAINT CK_NOTIFICACIONES_NUMERO
    CHECK (numero > 0);
/* TTipoNotificacion */
ALTER TABLE notificaciones ADD CONSTRAINT CK_NOTIFICACIONES_TIPO
    CHECK (tipo IN ('INFORMATIVA', 'ALERTA', 'RECORDATORIO'));


/* ======================================================================== */
/* Tuplas                                                                   */
/* ======================================================================== */
/* Llaves primarias */
ALTER TABLE usuarios ADD CONSTRAINT PK_USUARIOS
    PRIMARY KEY (idUsuario);
ALTER TABLE planesFormacion ADD CONSTRAINT PK_PLANESFORMACION
    PRIMARY KEY (codigo);
ALTER TABLE actividades ADD CONSTRAINT PK_ACTIVIDADES
    PRIMARY KEY (plan, numero);
ALTER TABLE inscripciones ADD CONSTRAINT PK_INSCRIPCIONES
    PRIMARY KEY (plan, usuario);
ALTER TABLE notificaciones ADD CONSTRAINT PK_NOTIFICACIONES
    PRIMARY KEY (numero);

/* Llaves unicas */
ALTER TABLE usuarios ADD CONSTRAINT UK_USUARIOS_CORREO
    UNIQUE (correo);
ALTER TABLE planesFormacion ADD CONSTRAINT UK_PLANESFORMACION_NOMBRE
    UNIQUE (nombre);
ALTER TABLE actividades ADD CONSTRAINT UK_ACTIVIDADES_NOMBRE
    UNIQUE (plan, nombre);

/* Restricciones de tupla */
/* La fecha de fin no puede ser anterior a la de inicio */
ALTER TABLE planesFormacion ADD CONSTRAINT CK_PLANESFORMACION_FECHAS
    CHECK (fechaFin IS NULL OR fechaInicio IS NULL OR fechaFin >= fechaInicio);
/* Un plan inicia en o despues de su creacion */
ALTER TABLE planesFormacion ADD CONSTRAINT CK_PLANESFORMACION_INICIO
    CHECK (fechaInicio IS NULL OR fechaInicio >= fechaCreacion);
/* Un plan activo o finalizado debe tener sus fechas definidas */
ALTER TABLE planesFormacion ADD CONSTRAINT CK_PLANESFORMACION_ESTADOFECHAS
    CHECK (estado IN ('B', 'C') OR (fechaInicio IS NOT NULL AND fechaFin IS NOT NULL));
/* Una notificacion tiene fecha de lectura si y solo si fue leida */
ALTER TABLE notificaciones ADD CONSTRAINT CK_NOTIFICACIONES_LECTURA
    CHECK ((leida AND fechaLectura IS NOT NULL AND fechaLectura >= fecha)
        OR (NOT leida AND fechaLectura IS NULL));


/* ======================================================================== */
/* Acciones                                                                 */
/* ======================================================================== */
/* Las actividades e inscripciones son parte del plan: se eliminan con el */
ALTER TABLE planesFormacion ADD CONSTRAINT FK_PLANESFORMACION_USUARIOS
    FOREIGN KEY (responsable) REFERENCES usuarios (idUsuario);
ALTER TABLE actividades ADD CONSTRAINT FK_ACTIVIDADES_PLANESFORMACION
    FOREIGN KEY (plan) REFERENCES planesFormacion (codigo)
    ON DELETE CASCADE;
ALTER TABLE inscripciones ADD CONSTRAINT FK_INSCRIPCIONES_PLANESFORMACION
    FOREIGN KEY (plan) REFERENCES planesFormacion (codigo)
    ON DELETE CASCADE;
ALTER TABLE inscripciones ADD CONSTRAINT FK_INSCRIPCIONES_USUARIOS
    FOREIGN KEY (usuario) REFERENCES usuarios (idUsuario)
    ON DELETE CASCADE;
/* Las notificaciones pertenecen al usuario; si el plan desaparece se conservan */
ALTER TABLE notificaciones ADD CONSTRAINT FK_NOTIFICACIONES_USUARIOS
    FOREIGN KEY (usuario) REFERENCES usuarios (idUsuario)
    ON DELETE CASCADE;
ALTER TABLE notificaciones ADD CONSTRAINT FK_NOTIFICACIONES_PLANESFORMACION
    FOREIGN KEY (plan) REFERENCES planesFormacion (codigo)
    ON DELETE SET NULL;


/* ======================================================================== */
/* Disparadores                                                             */
/* ======================================================================== */

/* ---------------- Caso de uso 1: Mantener plan de formacion ------------- */

/* Adicionar plan:
 *  - El codigo se genera automaticamente (PF + consecutivo de 4 digitos,
 *    tomado de SEQ_PLANESFORMACION para no reutilizar codigos eliminados).
 *  - La fecha de creacion es la fecha actual.
 *  - Todo plan nace en estado Borrador.
 *  - El responsable debe ser profesor o administrador. */
CREATE FUNCTION FN_PLANESFORMACION_BI() RETURNS TRIGGER AS $$
DECLARE
    vRol CHAR(1);
BEGIN
    SELECT rol INTO vRol FROM usuarios WHERE idUsuario = NEW.responsable;
    IF vRol = 'E' THEN
        RAISE EXCEPTION 'El responsable de un plan debe ser profesor o administrador';
    END IF;
    NEW.codigo := 'PF' || LPAD(NEXTVAL('SEQ_PLANESFORMACION')::TEXT, 4, '0');
    NEW.fechaCreacion := CURRENT_DATE;
    NEW.estado := 'B';
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER TR_PLANESFORMACION_BI
    BEFORE INSERT ON planesFormacion
    FOR EACH ROW EXECUTE FUNCTION FN_PLANESFORMACION_BI();

/* Modificar plan:
 *  - El codigo y la fecha de creacion no se pueden modificar.
 *  - Un plan finalizado o cancelado no se puede modificar.
 *  - Transiciones validas: B->A, A->F, B->C, A->C.
 *  - Para activar un plan debe tener al menos una actividad obligatoria.
 *  - Las fechas de un plan activo no se pueden modificar.
 *  - El responsable debe ser profesor o administrador. */
CREATE FUNCTION FN_PLANESFORMACION_BU() RETURNS TRIGGER AS $$
DECLARE
    vRol CHAR(1);
BEGIN
    IF NEW.codigo <> OLD.codigo OR NEW.fechaCreacion <> OLD.fechaCreacion THEN
        RAISE EXCEPTION 'El codigo y la fecha de creacion de un plan no se pueden modificar';
    END IF;
    IF OLD.estado IN ('F', 'C') THEN
        RAISE EXCEPTION 'El plan % esta finalizado o cancelado y no se puede modificar', OLD.codigo;
    END IF;
    IF NEW.estado <> OLD.estado AND NOT (
           (OLD.estado = 'B' AND NEW.estado IN ('A', 'C'))
        OR (OLD.estado = 'A' AND NEW.estado IN ('F', 'C'))) THEN
        RAISE EXCEPTION 'Transicion de estado invalida: % -> %', OLD.estado, NEW.estado;
    END IF;
    IF NEW.estado = 'A' AND OLD.estado = 'B' AND NOT EXISTS (
           SELECT 1 FROM actividades WHERE plan = NEW.codigo AND obligatoria) THEN
        RAISE EXCEPTION 'Un plan debe tener al menos una actividad obligatoria para activarse';
    END IF;
    IF OLD.estado = 'A' AND (NEW.fechaInicio IS DISTINCT FROM OLD.fechaInicio
                          OR NEW.fechaFin IS DISTINCT FROM OLD.fechaFin) THEN
        RAISE EXCEPTION 'Las fechas de un plan activo no se pueden modificar';
    END IF;
    IF NEW.responsable <> OLD.responsable THEN
        SELECT rol INTO vRol FROM usuarios WHERE idUsuario = NEW.responsable;
        IF vRol = 'E' THEN
            RAISE EXCEPTION 'El responsable de un plan debe ser profesor o administrador';
        END IF;
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER TR_PLANESFORMACION_BU
    BEFORE UPDATE ON planesFormacion
    FOR EACH ROW EXECUTE FUNCTION FN_PLANESFORMACION_BU();

/* Eliminar plan:
 *  - Solo se pueden eliminar planes en borrador o cancelados.
 *  - Un plan en borrador con inscritos no se puede eliminar (se debe cancelar). */
CREATE FUNCTION FN_PLANESFORMACION_BD() RETURNS TRIGGER AS $$
BEGIN
    IF OLD.estado NOT IN ('B', 'C') THEN
        RAISE EXCEPTION 'Solo se pueden eliminar planes en borrador o cancelados';
    END IF;
    IF OLD.estado = 'B' AND EXISTS (
           SELECT 1 FROM inscripciones WHERE plan = OLD.codigo) THEN
        RAISE EXCEPTION 'El plan % tiene inscritos; debe cancelarse en lugar de eliminarse', OLD.codigo;
    END IF;
    RETURN OLD;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER TR_PLANESFORMACION_BD
    BEFORE DELETE ON planesFormacion
    FOR EACH ROW EXECUTE FUNCTION FN_PLANESFORMACION_BD();

/* Adicionar actividad:
 *  - El numero es el consecutivo dentro del plan.
 *  - Solo se adicionan actividades a planes en borrador o activos. */
CREATE FUNCTION FN_ACTIVIDADES_BI() RETURNS TRIGGER AS $$
DECLARE
    vEstado CHAR(1);
BEGIN
    SELECT estado INTO vEstado FROM planesFormacion WHERE codigo = NEW.plan;
    IF vEstado IN ('F', 'C') THEN
        RAISE EXCEPTION 'No se pueden adicionar actividades a un plan finalizado o cancelado';
    END IF;
    SELECT COALESCE(MAX(numero), 0) + 1 INTO NEW.numero
      FROM actividades WHERE plan = NEW.plan;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER TR_ACTIVIDADES_BI
    BEFORE INSERT ON actividades
    FOR EACH ROW EXECUTE FUNCTION FN_ACTIVIDADES_BI();

/* Modificar actividad:
 *  - El plan y el numero no se pueden modificar.
 *  - Solo se modifican actividades de planes en borrador. */
CREATE FUNCTION FN_ACTIVIDADES_BU() RETURNS TRIGGER AS $$
BEGIN
    IF NEW.plan <> OLD.plan OR NEW.numero <> OLD.numero THEN
        RAISE EXCEPTION 'El plan y el numero de una actividad no se pueden modificar';
    END IF;
    IF (SELECT estado FROM planesFormacion WHERE codigo = OLD.plan) <> 'B' THEN
        RAISE EXCEPTION 'Solo se modifican actividades de planes en borrador';
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER TR_ACTIVIDADES_BU
    BEFORE UPDATE ON actividades
    FOR EACH ROW EXECUTE FUNCTION FN_ACTIVIDADES_BU();

/* Eliminar actividad:
 *  - Solo se eliminan actividades de planes en borrador,
 *    salvo que se este eliminando el plan completo (cascada). */
CREATE FUNCTION FN_ACTIVIDADES_BD() RETURNS TRIGGER AS $$
BEGIN
    IF pg_trigger_depth() = 1
       AND (SELECT estado FROM planesFormacion WHERE codigo = OLD.plan) <> 'B' THEN
        RAISE EXCEPTION 'Solo se eliminan actividades de planes en borrador';
    END IF;
    RETURN OLD;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER TR_ACTIVIDADES_BD
    BEFORE DELETE ON actividades
    FOR EACH ROW EXECUTE FUNCTION FN_ACTIVIDADES_BD();

/* Inscribir usuario en un plan:
 *  - La fecha es la actual y el estado inicial es Inscrito.
 *  - Solo se inscribe en planes en borrador o activos.
 *  - Solo se inscriben estudiantes. */
CREATE FUNCTION FN_INSCRIPCIONES_BI() RETURNS TRIGGER AS $$
BEGIN
    IF (SELECT estado FROM planesFormacion WHERE codigo = NEW.plan) IN ('F', 'C') THEN
        RAISE EXCEPTION 'No se puede inscribir en un plan finalizado o cancelado';
    END IF;
    IF (SELECT rol FROM usuarios WHERE idUsuario = NEW.usuario) <> 'E' THEN
        RAISE EXCEPTION 'Solo los estudiantes se inscriben en planes de formacion';
    END IF;
    NEW.fecha := CURRENT_DATE;
    NEW.estado := 'I';
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER TR_INSCRIPCIONES_BI
    BEFORE INSERT ON inscripciones
    FOR EACH ROW EXECUTE FUNCTION FN_INSCRIPCIONES_BI();

/* ---------------- [BONO] Caso de uso 2: Registrar notificacion ---------- */

/* Registrar notificacion:
 *  - El numero es un consecutivo automatico.
 *  - La fecha es el momento actual.
 *  - Toda notificacion nace sin leer.
 *  - Si esta asociada a un plan, el usuario debe estar inscrito
 *    o ser el responsable del plan. */
CREATE FUNCTION FN_NOTIFICACIONES_BI() RETURNS TRIGGER AS $$
BEGIN
    IF NEW.plan IS NOT NULL
       AND NOT EXISTS (SELECT 1 FROM inscripciones
                        WHERE plan = NEW.plan AND usuario = NEW.usuario)
       AND NOT EXISTS (SELECT 1 FROM planesFormacion
                        WHERE codigo = NEW.plan AND responsable = NEW.usuario) THEN
        RAISE EXCEPTION 'El usuario % no esta relacionado con el plan %', NEW.usuario, NEW.plan;
    END IF;
    NEW.numero := NEXTVAL('SEQ_NOTIFICACIONES');
    NEW.fecha := LOCALTIMESTAMP;
    NEW.leida := FALSE;
    NEW.fechaLectura := NULL;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER TR_NOTIFICACIONES_BI
    BEFORE INSERT ON notificaciones
    FOR EACH ROW EXECUTE FUNCTION FN_NOTIFICACIONES_BI();

/* Modificar notificacion:
 *  - Solo se puede marcar como leida (una sola vez); el resto es inmutable.
 *  - La fecha de lectura se asigna automaticamente. */
CREATE FUNCTION FN_NOTIFICACIONES_BU() RETURNS TRIGGER AS $$
BEGIN
    IF NEW.numero <> OLD.numero OR NEW.usuario <> OLD.usuario
       OR NEW.fecha <> OLD.fecha OR NEW.tipo <> OLD.tipo
       OR NEW.asunto <> OLD.asunto OR NEW.mensaje <> OLD.mensaje
       OR (NEW.plan IS DISTINCT FROM OLD.plan AND NEW.plan IS NOT NULL) THEN
        RAISE EXCEPTION 'Una notificacion solo se puede marcar como leida';
    END IF;
    IF OLD.leida AND NOT NEW.leida THEN
        RAISE EXCEPTION 'Una notificacion leida no puede volver a estar sin leer';
    END IF;
    IF NEW.leida AND NOT OLD.leida THEN
        NEW.fechaLectura := LOCALTIMESTAMP;
    ELSE
        NEW.fechaLectura := OLD.fechaLectura;
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER TR_NOTIFICACIONES_BU
    BEFORE UPDATE ON notificaciones
    FOR EACH ROW EXECUTE FUNCTION FN_NOTIFICACIONES_BU();

/* Eliminar notificacion:
 *  - Solo se eliminan notificaciones leidas
 *    (salvo cascada por eliminacion del usuario). */
CREATE FUNCTION FN_NOTIFICACIONES_BD() RETURNS TRIGGER AS $$
BEGIN
    IF pg_trigger_depth() = 1 AND NOT OLD.leida THEN
        RAISE EXCEPTION 'Solo se pueden eliminar notificaciones leidas';
    END IF;
    RETURN OLD;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER TR_NOTIFICACIONES_BD
    BEFORE DELETE ON notificaciones
    FOR EACH ROW EXECUTE FUNCTION FN_NOTIFICACIONES_BD();

/* Automatizacion: al activar, finalizar o cancelar un plan se notifica
 * a los inscritos (no retirados) y al responsable. */
CREATE FUNCTION FN_PLANESFORMACION_AU() RETURNS TRIGGER AS $$
DECLARE
    vAsunto  VARCHAR(80);
    vMensaje VARCHAR(500);
    vTipo    VARCHAR(12);
BEGIN
    IF NEW.estado = OLD.estado THEN
        RETURN NEW;
    END IF;
    CASE NEW.estado
        WHEN 'A' THEN
            vTipo := 'INFORMATIVA';
            vAsunto := 'Plan ' || NEW.codigo || ' activado';
            vMensaje := 'El plan "' || NEW.nombre || '" inicia el ' || NEW.fechaInicio
                     || ' y finaliza el ' || NEW.fechaFin || '.';
        WHEN 'F' THEN
            vTipo := 'RECORDATORIO';
            vAsunto := 'Plan ' || NEW.codigo || ' finalizado';
            vMensaje := 'El plan "' || NEW.nombre || '" ha finalizado.';
        WHEN 'C' THEN
            vTipo := 'ALERTA';
            vAsunto := 'Plan ' || NEW.codigo || ' cancelado';
            vMensaje := 'El plan "' || NEW.nombre || '" ha sido cancelado.';
        ELSE
            RETURN NEW;
    END CASE;
    INSERT INTO notificaciones (usuario, plan, tipo, asunto, mensaje)
        SELECT usuario, NEW.codigo, vTipo, vAsunto, vMensaje
          FROM inscripciones
         WHERE plan = NEW.codigo AND estado <> 'R'
        UNION
        SELECT NEW.responsable, NEW.codigo, vTipo, vAsunto, vMensaje;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER TR_PLANESFORMACION_AU
    AFTER UPDATE ON planesFormacion
    FOR EACH ROW EXECUTE FUNCTION FN_PLANESFORMACION_AU();


/* ======================================================================== */
/* TuplasOK                                                                 */
/* ======================================================================== */
/* Usuarios de todos los roles con correos validos */
INSERT INTO usuarios (idUsuario, nombre, correo, rol) VALUES
    (1, 'Ana Torres',     'ana.torres@agisworld.com',    'A'),
    (2, 'Carlos Ruiz',    'carlos.ruiz@agisworld.com',   'P'),
    (3, 'Laura Gomez',    'laura.gomez@mail.escuelaing.edu.co', 'E'),
    (4, 'Pedro Diaz',     'pedro.diaz@mail.escuelaing.edu.co',  'E'),
    (5, 'Sofia Herrera',  'sofia.herrera@mail.escuelaing.edu.co', 'E'),
    (6, 'Miguel Castro',  'miguel.castro@agisworld.com', 'P');

/* Planes sin fechas (borrador) y con fechas coherentes;
 * el codigo, la fecha de creacion y el estado los asigna el disparador */
INSERT INTO planesFormacion (codigo, nombre, descripcion, nivel, fechaInicio, fechaFin, responsable) VALUES
    ('PF0000', 'Fundamentos de bases de datos', 'Modelo relacional y SQL basico', 'B',
     CURRENT_DATE + 10, CURRENT_DATE + 70, 2),
    ('PF0000', 'Analitica de datos', 'Consultas analiticas y visualizacion', 'I',
     CURRENT_DATE + 20, CURRENT_DATE + 110, 6),
    ('PF0000', 'Arquitectura de datos', NULL, 'A', NULL, NULL, 1);

/* Actividades: el numero lo asigna el disparador */
INSERT INTO actividades (plan, numero, nombre, tipo, horas, obligatoria) VALUES
    ('PF0001', 0, 'Modelo entidad relacion', 'CURSO',      20, TRUE),
    ('PF0001', 0, 'Taller de SQL',           'TALLER',     16, TRUE),
    ('PF0001', 0, 'Proyecto final',          'PROYECTO',   30, FALSE),
    ('PF0002', 0, 'Consultas analiticas',    'CURSO',      24, TRUE),
    ('PF0002', 0, 'Evaluacion final',        'EVALUACION',  4, TRUE);

INSERT INTO inscripciones (plan, usuario, fecha, estado) VALUES
    ('PF0001', 3, CURRENT_DATE, 'I'),
    ('PF0001', 4, CURRENT_DATE, 'I'),
    ('PF0002', 5, CURRENT_DATE, 'I');


/* ======================================================================== */
/* TuplasNoOK  (cada instruccion DEBE fallar)                               */
/* ======================================================================== */
/* CK_USUARIOS_CORREO: correo sin dominio */
INSERT INTO usuarios (idUsuario, nombre, correo, rol)
    VALUES (10, 'Correo Malo', 'correo.sin.arroba', 'E');
/* CK_USUARIOS_ROL: rol inexistente */
INSERT INTO usuarios (idUsuario, nombre, correo, rol)
    VALUES (11, 'Rol Malo', 'rol.malo@agisworld.com', 'X');
/* PK_USUARIOS: identificador repetido */
INSERT INTO usuarios (idUsuario, nombre, correo, rol)
    VALUES (1, 'Repetido', 'repetido@agisworld.com', 'E');
/* UK_USUARIOS_CORREO: correo repetido */
INSERT INTO usuarios (idUsuario, nombre, correo, rol)
    VALUES (12, 'Otra Ana', 'ana.torres@agisworld.com', 'E');
/* CK_PLANESFORMACION_NIVEL: nivel inexistente */
INSERT INTO planesFormacion (codigo, nombre, nivel, responsable)
    VALUES ('PF0000', 'Plan nivel malo', 'Z', 2);
/* CK_PLANESFORMACION_FECHAS: fecha fin antes de fecha inicio */
INSERT INTO planesFormacion (codigo, nombre, nivel, fechaInicio, fechaFin, responsable)
    VALUES ('PF0000', 'Plan fechas malas', 'B', CURRENT_DATE + 30, CURRENT_DATE + 5, 2);
/* UK_PLANESFORMACION_NOMBRE: nombre repetido */
INSERT INTO planesFormacion (codigo, nombre, nivel, responsable)
    VALUES ('PF0000', 'Analitica de datos', 'B', 2);
/* FK_PLANESFORMACION_USUARIOS: responsable inexistente */
INSERT INTO planesFormacion (codigo, nombre, nivel, responsable)
    VALUES ('PF0000', 'Plan sin responsable', 'B', 99);
/* CK_ACTIVIDADES_HORAS: horas fuera de rango */
INSERT INTO actividades (plan, numero, nombre, tipo, horas)
    VALUES ('PF0001', 0, 'Actividad eterna', 'CURSO', 500);
/* CK_ACTIVIDADES_TIPO: tipo inexistente */
INSERT INTO actividades (plan, numero, nombre, tipo, horas)
    VALUES ('PF0001', 0, 'Actividad rara', 'PASEO', 5);
/* UK_ACTIVIDADES_NOMBRE: nombre repetido dentro del plan */
INSERT INTO actividades (plan, numero, nombre, tipo, horas)
    VALUES ('PF0001', 0, 'Taller de SQL', 'TALLER', 8);
/* FK_ACTIVIDADES_PLANESFORMACION: plan inexistente */
INSERT INTO actividades (plan, numero, nombre, tipo, horas)
    VALUES ('PF9999', 0, 'Huerfana', 'CURSO', 5);
/* PK_INSCRIPCIONES: el usuario ya esta inscrito en el plan */
INSERT INTO inscripciones (plan, usuario, fecha, estado)
    VALUES ('PF0001', 3, CURRENT_DATE, 'I');
/* CK_NOTIFICACIONES_TIPO: tipo inexistente */
INSERT INTO notificaciones (numero, usuario, fecha, tipo, asunto, mensaje, leida)
    VALUES (1, 3, LOCALTIMESTAMP, 'SPAM', 'Hola', 'Mensaje', FALSE);


/* ======================================================================== */
/* AccionesOK                                                               */
/* ======================================================================== */
/* Al eliminar un plan en borrador sin inscritos se eliminan sus actividades */
INSERT INTO planesFormacion (codigo, nombre, nivel, responsable)
    VALUES ('PF0000', 'Plan temporal', 'B', 2);
INSERT INTO actividades (plan, numero, nombre, tipo, horas)
    SELECT codigo, 0, 'Actividad temporal', 'CURSO', 2
      FROM planesFormacion WHERE nombre = 'Plan temporal';
DELETE FROM planesFormacion WHERE nombre = 'Plan temporal';
SELECT COUNT(*) AS actividadesTemporales FROM actividades
 WHERE nombre = 'Actividad temporal';                                       -- 0

/* Al eliminar un usuario se eliminan sus inscripciones y notificaciones */
INSERT INTO usuarios (idUsuario, nombre, correo, rol)
    VALUES (7, 'Usuario Temporal', 'temporal@agisworld.com', 'E');
INSERT INTO inscripciones (plan, usuario, fecha, estado)
    VALUES ('PF0003', 7, CURRENT_DATE, 'I');
INSERT INTO notificaciones (usuario, plan, tipo, asunto, mensaje)
    VALUES (7, 'PF0003', 'INFORMATIVA', 'Bienvenida', 'Bienvenido al plan');
DELETE FROM usuarios WHERE idUsuario = 7;
SELECT COUNT(*) AS inscripciones7 FROM inscripciones WHERE usuario = 7;      -- 0
SELECT COUNT(*) AS notificaciones7 FROM notificaciones WHERE usuario = 7;    -- 0


/* ======================================================================== */
/* DisparadoresOK                                                           */
/* ======================================================================== */
/* Codigos, fecha de creacion y estado generados automaticamente */
SELECT codigo, nombre, fechaCreacion, estado FROM planesFormacion ORDER BY codigo;
/* Numeros de actividad consecutivos por plan */
SELECT plan, numero, nombre FROM actividades ORDER BY plan, numero;

/* Modificar datos de un plan en borrador */
UPDATE planesFormacion SET descripcion = 'Modelo relacional, SQL-DDL y SQL-DML'
 WHERE codigo = 'PF0001';
/* Modificar una actividad de un plan en borrador */
UPDATE actividades SET horas = 18 WHERE plan = 'PF0001' AND numero = 2;
/* Eliminar una actividad de un plan en borrador */
DELETE FROM actividades WHERE plan = 'PF0001' AND numero = 3;

/* Activar un plan con actividades obligatoria: notifica inscritos y responsable */
UPDATE planesFormacion SET estado = 'A' WHERE codigo = 'PF0001';
SELECT numero, usuario, plan, tipo, asunto, leida FROM notificaciones
 WHERE plan = 'PF0001' ORDER BY numero;                                    -- 3 notificaciones

/* Adicionar actividad a un plan activo */
INSERT INTO actividades (plan, numero, nombre, tipo, horas)
    VALUES ('PF0001', 0, 'Evaluacion final', 'EVALUACION', 2);

/* Cancelar un plan en borrador y luego eliminarlo */
UPDATE planesFormacion SET estado = 'C' WHERE codigo = 'PF0003';
DELETE FROM planesFormacion WHERE codigo = 'PF0003';

/* [BONO] Registrar notificacion: numero, fecha y leida automaticos */
INSERT INTO notificaciones (numero, usuario, plan, fecha, tipo, asunto, mensaje, leida)
    VALUES (0, 5, 'PF0002', '2000-01-01', 'RECORDATORIO', 'Inicio proximo',
            'Su plan inicia pronto', TRUE);
INSERT INTO notificaciones (usuario, tipo, asunto, mensaje)
    VALUES (4, 'INFORMATIVA', 'Bienvenida', 'Bienvenido a AGISWorld');
/* Marcar como leida: la fecha de lectura es automatica */
UPDATE notificaciones SET leida = TRUE
 WHERE usuario = 4 AND asunto = 'Bienvenida';
/* Eliminar una notificacion leida */
DELETE FROM notificaciones WHERE usuario = 4 AND asunto = 'Bienvenida';
SELECT numero, usuario, plan, fecha, tipo, asunto, leida, fechaLectura
  FROM notificaciones ORDER BY numero;


/* ======================================================================== */
/* DisparadoresNoOK  (cada instruccion DEBE fallar)                         */
/* ======================================================================== */
/* El responsable de un plan no puede ser estudiante */
INSERT INTO planesFormacion (codigo, nombre, nivel, responsable)
    VALUES ('PF0000', 'Plan de estudiante', 'B', 3);
/* El codigo de un plan no se modifica */
UPDATE planesFormacion SET codigo = 'PF0099' WHERE codigo = 'PF0002';
/* La fecha de creacion de un plan no se modifica */
UPDATE planesFormacion SET fechaCreacion = CURRENT_DATE - 30 WHERE codigo = 'PF0002';
/* Transicion invalida: borrador -> finalizado */
UPDATE planesFormacion SET estado = 'F' WHERE codigo = 'PF0002';
/* Las fechas de un plan activo no se modifican */
UPDATE planesFormacion SET fechaFin = CURRENT_DATE + 200 WHERE codigo = 'PF0001';
/* No se activa un plan sin actividades obligatorias */
INSERT INTO planesFormacion (codigo, nombre, nivel, fechaInicio, fechaFin, responsable)
    VALUES ('PF0000', 'Plan vacio', 'B', CURRENT_DATE + 1, CURRENT_DATE + 5, 2);  -- (OK)
UPDATE planesFormacion SET estado = 'A' WHERE nombre = 'Plan vacio';
/* No se elimina un plan activo */
DELETE FROM planesFormacion WHERE codigo = 'PF0001';
/* No se elimina un plan en borrador con inscritos */
DELETE FROM planesFormacion WHERE codigo = 'PF0002';
/* No se modifica un plan cancelado */
UPDATE planesFormacion SET estado = 'C' WHERE nombre = 'Plan vacio';          -- (OK)
UPDATE planesFormacion SET nombre = 'Plan revivido' WHERE nombre = 'Plan vacio';
/* No se modifican actividades de un plan activo */
UPDATE actividades SET horas = 40 WHERE plan = 'PF0001' AND numero = 1;
/* No se eliminan actividades de un plan activo */
DELETE FROM actividades WHERE plan = 'PF0001' AND numero = 1;
/* No se adicionan actividades a un plan cancelado */
INSERT INTO actividades (plan, numero, nombre, tipo, horas)
    SELECT codigo, 0, 'Actividad tardia', 'CURSO', 5
      FROM planesFormacion WHERE nombre = 'Plan vacio';
/* Solo los estudiantes se inscriben */
INSERT INTO inscripciones (plan, usuario, fecha, estado)
    VALUES ('PF0002', 6, CURRENT_DATE, 'I');
/* [BONO] No se notifica a un usuario no relacionado con el plan */
INSERT INTO notificaciones (usuario, plan, tipo, asunto, mensaje)
    VALUES (3, 'PF0002', 'ALERTA', 'Ajeno', 'No deberia llegar');
/* [BONO] Una notificacion solo se puede marcar como leida */
UPDATE notificaciones SET mensaje = 'Mensaje alterado'
 WHERE usuario = 3 AND asunto = 'Plan PF0001 activado';
/* [BONO] Una notificacion leida no vuelve a estar sin leer */
UPDATE notificaciones SET leida = TRUE
 WHERE usuario = 3 AND asunto = 'Plan PF0001 activado';                     -- (OK)
UPDATE notificaciones SET leida = FALSE
 WHERE usuario = 3 AND asunto = 'Plan PF0001 activado';
/* [BONO] No se eliminan notificaciones sin leer */
DELETE FROM notificaciones WHERE usuario = 4 AND asunto = 'Plan PF0001 activado';


/* ======================================================================== */
/* Consultas                                                                */
/* ======================================================================== */
/* Consulta gerencial: para cada plan, horas totales, inscritos activos
 * y porcentaje de notificaciones leidas por sus usuarios */
SELECT p.codigo,
       p.nombre,
       p.estado,
       (SELECT COALESCE(SUM(a.horas), 0) FROM actividades a
         WHERE a.plan = p.codigo)                                    AS horasTotales,
       (SELECT COUNT(*) FROM inscripciones i
         WHERE i.plan = p.codigo AND i.estado <> 'R')               AS inscritos,
       (SELECT ROUND(100.0 * COUNT(*) FILTER (WHERE n.leida) / NULLIF(COUNT(*), 0), 1)
          FROM notificaciones n WHERE n.plan = p.codigo)            AS pctLeidas
  FROM planesFormacion p
 ORDER BY p.codigo;
