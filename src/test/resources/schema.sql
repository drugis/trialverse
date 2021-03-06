SET DATABASE SQL SYNTAX PGS TRUE;
SET DATABASE SQL SIZE FALSE;

-- changeset reidd:1

CREATE TABLE UserConnection (userId varchar(255) NOT NULL,
  providerId VARCHAR(255) NOT NULL,
  providerUserId VARCHAR(255),
  rank INT NOT NULL,
  displayName VARCHAR(255),
  profileUrl VARCHAR(512),
  imageUrl VARCHAR(512),
  accessToken VARCHAR(255) NOT NULL,
  secret VARCHAR(255),
  refreshToken VARCHAR(255),
  expireTime bigint,
  PRIMARY KEY (userId, providerId, providerUserId));
CREATE UNIQUE index UserConnectionRank ON UserConnection(userId, providerId, rank);

CREATE TABLE Account (id SERIAL NOT NULL,
            username VARCHAR UNIQUE,
            firstName VARCHAR NOT NULL,
            lastName VARCHAR NOT NULL,
            password VARCHAR DEFAULT '',
            PRIMARY KEY (id));

CREATE TABLE AccountRoles (
    accountId INT,
    role VARCHAR NOT NULL,
    FOREIGN KEY (accountId) REFERENCES Account(id)
);

-- changeset stroombergc:2

CREATE TABLE VersionMapping (id SERIAL NOT NULL,
    versionedDatasetUrl VARCHAR NOT NULL,
    ownerUuid VARCHAR NOT NULL,
    trialverseDatasetUrl VARCHAR NOT NULL,
    PRIMARY KEY (id));

-- changeset stroombergc:3

ALTER TABLE Account ADD userNameHash VARCHAR NOT NULL;
--rollback ALTER TABLE Account DROP COLUMN userNameHash;

-- changeset stroombergc:4

CREATE TABLE ApplicationKey (id SERIAL NOT NULL,
            secretKey VARCHAR UNIQUE,
            accountId INT NOT NULL,
            applicationName VARCHAR NOT NULL,
            creationDate DATE NOT NULL,
            revocationDate DATE NOT NULL,
            PRIMARY KEY (id),
            FOREIGN KEY (accountId) REFERENCES Account(id)
);
--rollback DROP TABLE ApplicationKey;
