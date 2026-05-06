CREATE TABLE dsa (
                     id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                     name VARCHAR(255) NOT NULL
);

CREATE TABLE geofence (
                          id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                          name VARCHAR(255) NOT NULL,
                          latitude BIGINT NOT NULL,
                          longitude BIGINT NOT NULL,
                          radius_in_metres BIGINT NOT NULL
);

CREATE TABLE dsa_geofence (
                              id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                              name VARCHAR(255),
                              dsa_id UUID NOT NULL REFERENCES dsa(id),
                              geofence_id UUID NOT NULL REFERENCES geofence(id)
);

CREATE TABLE dsa_session (
                             id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                             dsa_id UUID NOT NULL REFERENCES dsa(id),
                             start_time BIGINT NOT NULL,
                             checkout_id UUID NULL
);

CREATE TABLE checkout (
                          id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                          session_id UUID NOT NULL REFERENCES dsa_session(id),
                          end_time BIGINT NOT NULL,
                          total_distance_m BIGINT,
                          total_time_s BIGINT
);

CREATE TABLE location_data (
                               id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                               session_id UUID NOT NULL REFERENCES dsa_session(id),
                               latitude BIGINT NOT NULL,
                               longitude BIGINT NOT NULL,
                               recorded_at BIGINT NOT NULL
);

ALTER TABLE dsa_session
    ADD CONSTRAINT fk_session_checkout
        FOREIGN KEY (checkout_id) REFERENCES checkout(id);

-- Indexes
CREATE INDEX idx_location_data_session_id ON location_data(session_id);
CREATE INDEX idx_dsa_session_dsa_id ON dsa_session(dsa_id);
CREATE INDEX idx_dsa_geofence_dsa_id ON dsa_geofence(dsa_id);
CREATE INDEX idx_dsa_geofence_geofence_id ON dsa_geofence(geofence_id);