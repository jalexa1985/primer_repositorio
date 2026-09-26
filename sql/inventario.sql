-- =====================================================================
-- Modelo físico: Sistema de inventario de materia prima y recetas
-- Motor: MySQL 8 / MariaDB
-- Basado en el diagrama de clases (materiaPrima, inventario, almacen,
-- Receta, loteInventario) y el modelo lógico relacional.
-- =====================================================================

DROP DATABASE IF EXISTS inventario_db;
CREATE DATABASE inventario_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE inventario_db;

-- ---------------------------------------------------------------------
-- Almacenes (almacen_id, ubicacion_almacen, capacidad, empleados)
-- ---------------------------------------------------------------------
CREATE TABLE almacen (
    almacen_id         INT          NOT NULL AUTO_INCREMENT,
    ubicacion_almacen  VARCHAR(150) NOT NULL,
    capacidad          INT          NOT NULL,
    CONSTRAINT pk_almacen PRIMARY KEY (almacen_id),
    CONSTRAINT ck_almacen_capacidad CHECK (capacidad >= 0)
);

-- "empleados : String[]" es un atributo multivaluado -> tabla propia (1FN)
CREATE TABLE almacen_empleado (
    almacen_id       INT          NOT NULL,
    nombre_empleado  VARCHAR(100) NOT NULL,
    CONSTRAINT pk_almacen_empleado PRIMARY KEY (almacen_id, nombre_empleado),
    CONSTRAINT fk_empleado_almacen FOREIGN KEY (almacen_id)
        REFERENCES almacen (almacen_id)
        ON UPDATE CASCADE ON DELETE CASCADE
);

-- ---------------------------------------------------------------------
-- Inventarios (inventario_id, valor_inventario, cantidad_lotes,
--              persona_registra_inventario, almacen_id FK)
-- Relación "necesita": inventario 1 --- 1 almacen  -> FK UNIQUE
-- ---------------------------------------------------------------------
CREATE TABLE inventario (
    inventario_id                INT          NOT NULL AUTO_INCREMENT,
    valor_inventario             INT          NOT NULL DEFAULT 0,
    cantidad_lotes               INT          NOT NULL DEFAULT 0,
    persona_registra_inventario  VARCHAR(100) NOT NULL,
    almacen_id                   INT          NOT NULL,
    CONSTRAINT pk_inventario PRIMARY KEY (inventario_id),
    CONSTRAINT uq_inventario_almacen UNIQUE (almacen_id),
    CONSTRAINT fk_inventario_almacen FOREIGN KEY (almacen_id)
        REFERENCES almacen (almacen_id)
        ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT ck_inventario_valor  CHECK (valor_inventario >= 0),
    CONSTRAINT ck_inventario_lotes  CHECK (cantidad_lotes >= 0)
);

-- ---------------------------------------------------------------------
-- Materias Primas (producto_mp_id, esta_en_inventario, tipo, procesada,
--                  fecha_vencimiento, fecha_ingreso, cantidad_materia,
--                  inventario_id FK)
-- Relación "contiene": inventario 1 --- * materiaPrima
-- (reemplaza "productos_en_inventario : String[]" del inventario)
-- ---------------------------------------------------------------------
CREATE TABLE materia_prima (
    producto_mp_id      INT          NOT NULL AUTO_INCREMENT,
    esta_en_inventario  BOOLEAN      NOT NULL DEFAULT TRUE,
    tipo                VARCHAR(50)  NOT NULL,
    procesada           BOOLEAN      NOT NULL DEFAULT FALSE,
    fecha_vencimiento   DATE,
    fecha_ingreso       DATE         NOT NULL,
    cantidad_materia    INT          NOT NULL DEFAULT 0,
    inventario_id       INT,
    CONSTRAINT pk_materia_prima PRIMARY KEY (producto_mp_id),
    CONSTRAINT fk_mp_inventario FOREIGN KEY (inventario_id)
        REFERENCES inventario (inventario_id)
        ON UPDATE CASCADE ON DELETE SET NULL,
    CONSTRAINT ck_mp_cantidad CHECK (cantidad_materia >= 0),
    CONSTRAINT ck_mp_fechas   CHECK (fecha_vencimiento IS NULL
                                     OR fecha_vencimiento >= fecha_ingreso)
);

-- ---------------------------------------------------------------------
-- Lotes inventarios (loteinventario_id, fecha_fabricacion,
--                    fecha_caducacion, ubicacion_en_almacen, estado,
--                    inventario_id FK)
-- Relación "registra": inventario 1 --- 1..* loteInventario
-- ---------------------------------------------------------------------
CREATE TABLE lote_inventario (
    loteinventario_id     INT          NOT NULL AUTO_INCREMENT,
    fecha_fabricacion     DATE         NOT NULL,
    fecha_caducacion      DATE         NOT NULL,
    ubicacion_en_almacen  VARCHAR(100) NOT NULL,
    estado                VARCHAR(30)  NOT NULL DEFAULT 'DISPONIBLE',
    inventario_id         INT          NOT NULL,
    CONSTRAINT pk_lote_inventario PRIMARY KEY (loteinventario_id),
    CONSTRAINT fk_lote_inventario FOREIGN KEY (inventario_id)
        REFERENCES inventario (inventario_id)
        ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT ck_lote_fechas CHECK (fecha_caducacion >= fecha_fabricacion),
    CONSTRAINT ck_lote_estado CHECK (estado IN ('DISPONIBLE', 'RESERVADO',
                                                'VENCIDO', 'AGOTADO'))
);

-- ---------------------------------------------------------------------
-- Recetas (receta_id, titulo, pasos_preparacion, precio)
-- ---------------------------------------------------------------------
CREATE TABLE receta (
    receta_id          INT          NOT NULL AUTO_INCREMENT,
    titulo             VARCHAR(150) NOT NULL,
    pasos_preparacion  TEXT         NOT NULL,
    precio             INT          NOT NULL DEFAULT 0,
    CONSTRAINT pk_receta PRIMARY KEY (receta_id),
    CONSTRAINT ck_receta_precio CHECK (precio >= 0)
);

-- "ingredientes" (FK hacia materia prima) con relación "requiere":
-- Receta * --- 1..* materiaPrima  -> relación N:M -> tabla intermedia
CREATE TABLE receta_ingrediente (
    receta_id       INT NOT NULL,
    producto_mp_id  INT NOT NULL,
    cantidad        INT NOT NULL DEFAULT 1,
    CONSTRAINT pk_receta_ingrediente PRIMARY KEY (receta_id, producto_mp_id),
    CONSTRAINT fk_ri_receta FOREIGN KEY (receta_id)
        REFERENCES receta (receta_id)
        ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT fk_ri_materia_prima FOREIGN KEY (producto_mp_id)
        REFERENCES materia_prima (producto_mp_id)
        ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT ck_ri_cantidad CHECK (cantidad > 0)
);

-- =====================================================================
-- Datos de ejemplo
-- =====================================================================
INSERT INTO almacen (ubicacion_almacen, capacidad) VALUES
    ('Bodega Norte - Calle 10 #20-30', 5000),
    ('Bodega Sur - Carrera 5 #40-12', 3000);

INSERT INTO almacen_empleado (almacen_id, nombre_empleado) VALUES
    (1, 'Ana Gómez'), (1, 'Luis Pérez'), (2, 'Marta Ruiz');

INSERT INTO inventario (valor_inventario, cantidad_lotes,
                        persona_registra_inventario, almacen_id) VALUES
    (1500000, 2, 'Ana Gómez', 1),
    (800000,  1, 'Marta Ruiz', 2);

INSERT INTO materia_prima (esta_en_inventario, tipo, procesada,
                           fecha_vencimiento, fecha_ingreso,
                           cantidad_materia, inventario_id) VALUES
    (TRUE, 'Harina',  TRUE,  '2027-03-01', '2026-09-01', 200, 1),
    (TRUE, 'Azúcar',  TRUE,  '2027-06-01', '2026-09-01', 150, 1),
    (TRUE, 'Huevos',  FALSE, '2026-10-15', '2026-09-20', 360, 2);

INSERT INTO lote_inventario (fecha_fabricacion, fecha_caducacion,
                             ubicacion_en_almacen, estado,
                             inventario_id) VALUES
    ('2026-08-25', '2027-03-01', 'Pasillo A - Estante 1', 'DISPONIBLE', 1),
    ('2026-08-28', '2027-06-01', 'Pasillo A - Estante 2', 'DISPONIBLE', 1),
    ('2026-09-18', '2026-10-15', 'Cuarto frío 1',         'RESERVADO',  2);

INSERT INTO receta (titulo, pasos_preparacion, precio) VALUES
    ('Torta básica', '1. Mezclar ingredientes. 2. Hornear 40 min a 180°C.', 25000);

INSERT INTO receta_ingrediente (receta_id, producto_mp_id, cantidad) VALUES
    (1, 1, 2), (1, 2, 1), (1, 3, 4);

-- =====================================================================
-- Consultas de ejemplo
-- =====================================================================
-- Ingredientes de una receta
-- SELECT r.titulo, mp.tipo, ri.cantidad
-- FROM receta r
-- JOIN receta_ingrediente ri ON ri.receta_id = r.receta_id
-- JOIN materia_prima mp      ON mp.producto_mp_id = ri.producto_mp_id;

-- Productos en inventario por almacén (antes productos_en_inventario[])
-- SELECT a.ubicacion_almacen, i.inventario_id, mp.tipo, mp.cantidad_materia
-- FROM almacen a
-- JOIN inventario i     ON i.almacen_id = a.almacen_id
-- JOIN materia_prima mp ON mp.inventario_id = i.inventario_id;
