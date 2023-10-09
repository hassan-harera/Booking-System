CREATE TABLE trip
(
    id                INT NOT NULL PRIMARY KEY,
    arabic_name       VARCHAR(100),
    english_name      VARCHAR(100),
    type              VARCHAR(10),
    line_id           INT NOT NULL,
    is_going          BOOLEAN,
    service_degree_id INT,
    bus_id            INT,
    driver_id         INT,
    start_at          DATETIME,
    end_at            DATETIME,
    seat_number       INT,
    status_id         INT,
    days              VARCHAR(200),
    remark            VARCHAR(300),
    start_time        TIME,
    end_time          TIME,
    reserve_type_id   INT,
    service_id        INT
);

CREATE TABLE trip_stations
(
    id                 INT NOT NULL,
    area_id            INT,
    line_no            INT NOT NULL,
    booking_office_id  INT,
    station_type_id    INT,
    reservation_office INT(1),
    start_time         DATETIME,
    end_time           DATETIME,
    PRIMARY KEY (id, line_no),
    FOREIGN KEY (id) REFERENCES trip (id),
    FOREIGN KEY (booking_office_id) REFERENCES booking_office (id)
);

CREATE TABLE trip_seat
(
    seat_number INT PRIMARY KEY NOT NULL,
    row_no      INT             NOT NULL,
    col_no      INT             NOT NULL,
    seat_no     INT             NOT NULL,
    seat_name   VARCHAR(300),
    trip_id     BIGINT,
    FOREIGN KEY (trip_id) REFERENCES trip (id)
);

CREATE TABLE reservation
(
    id                BIGINT PRIMARY KEY AUTO_INCREMENT,
    seat_id           INT,
    reservation_date  DATE,
    departure_time    INT,
    arrival_time      DATE,
    departure_station INT,
    arrival_station   INT,
    user_id           BIGINT,
    FOREIGN KEY (seat_id) REFERENCES trip_seat (seat_number),
    FOREIGN KEY (departure_station) REFERENCES trip_stations (id),
    FOREIGN KEY (arrival_station) REFERENCES trip_stations (id),
    FOREIGN KEY (user_id) REFERENCES user (id)
);

CREATE TABLE reservation_status
(
    reservation_status_id          INT NOT NULL PRIMARY KEY,
    reservation_status_name        VARCHAR(300),
    reservation_status_description VARCHAR(300),
    reservation_status_color       VARCHAR(300)
);

CREATE TABLE seat_pricing
(
    line_id           INT,
    category_id       INT,
    departure_station INT,
    arrival_station   INT,
    FOREIGN KEY (departure_station) REFERENCES trip_stations (id),
    FOREIGN KEY (arrival_station) REFERENCES trip_stations (id),
    FOREIGN KEY (line_id) REFERENCES line (id),
    FOREIGN KEY (category_id) REFERENCES category (id)
);

CREATE TABLE category
(
    id           INT PRIMARY KEY AUTO_INCREMENT,
    arabic_name  VARCHAR(50),
    english_name VARCHAR(50)
);

CREATE TABLE service
(
    id           INT PRIMARY KEY AUTO_INCREMENT,
    arabic_name  VARCHAR(50),
    english_name VARCHAR(50)
);

CREATE TABLE category_services
(
    category_id INT,
    service_id  INT,
    FOREIGN KEY (category_id) REFERENCES category (id),
    FOREIGN KEY (service_id) REFERENCES service (id)
);

CREATE TABLE line
(
    id           INT PRIMARY KEY AUTO_INCREMENT,
    arabic_name  VARCHAR(50),
    english_name VARCHAR(50),
    arabic_desc  VARCHAR(300),
    english_desc VARCHAR(300),
    line_code    VARCHAR(15),
    created_at   DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at   DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

