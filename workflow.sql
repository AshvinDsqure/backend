-- Table: public.workflowprocess

-- DROP TABLE IF EXISTS public.workflowprocess;

CREATE TABLE IF NOT EXISTS public.workflowprocess
(
    workflow_id integer,
    uuid uuid NOT NULL DEFAULT gen_random_uuid(),
    subject character varying COLLATE pg_catalog."default",
    init_date timestamp with time zone,
    item uuid,
    submitter_id uuid,
    assignduedate timestamp with time zone,
    priority character varying COLLATE pg_catalog."default",
    dispatchmode character varying COLLATE pg_catalog."default",
    workflowprocesssenderdiary uuid,
    workflowprocesscorrespondence uuid,
    CONSTRAINT workflowprocess_pkey PRIMARY KEY (uuid)
)

TABLESPACE pg_default;

ALTER TABLE IF EXISTS public.workflowprocess
    OWNER to dspace;
    
    -- Table: public.workflowprocesscorrespondence

-- DROP TABLE IF EXISTS public.workflowprocesscorrespondence;

CREATE TABLE IF NOT EXISTS public.workflowprocesscorrespondence
(
    workflowprocesscorrespondence_id integer,
    uuid uuid NOT NULL DEFAULT gen_random_uuid(),
    diarynumber character varying COLLATE pg_catalog."default",
    officelocation character varying COLLATE pg_catalog."default",
    diarydate timestamp with time zone,
    CONSTRAINT workflowprocesscorrespondence_pkey PRIMARY KEY (uuid)
)

TABLESPACE pg_default;

ALTER TABLE IF EXISTS public.workflowprocesscorrespondence
    OWNER to dspace;
    
    -- Table: public.workflowprocessdefinition

-- DROP TABLE IF EXISTS public.workflowprocessdefinition;

CREATE TABLE IF NOT EXISTS public.workflowprocessdefinition
(
    uuid uuid NOT NULL DEFAULT gen_random_uuid(),
    workflowprocessdefinition_id integer,
    workflowprocessdefinitionname character varying COLLATE pg_catalog."default",
    CONSTRAINT workflowprocessdefinition_pkey PRIMARY KEY (uuid)
)

TABLESPACE pg_default;

ALTER TABLE IF EXISTS public.workflowprocessdefinition
    OWNER to dspace;
    
    -- Table: public.workflowprocessdefinitioneperson

-- DROP TABLE IF EXISTS public.workflowprocessdefinitioneperson;

CREATE TABLE IF NOT EXISTS public.workflowprocessdefinitioneperson
(
    uuid uuid NOT NULL DEFAULT gen_random_uuid(),
    workflowprocessdefinitioneperson_id integer,
    eperson uuid,
    workflowprocessdefinition uuid,
    index integer,
    CONSTRAINT workflowprocessdefinitioneperson_pkey PRIMARY KEY (uuid)
)

TABLESPACE pg_default;

ALTER TABLE IF EXISTS public.workflowprocessdefinitioneperson
    OWNER to dspace;
    
    -- Table: public.workflowprocessreferencedoc

-- DROP TABLE IF EXISTS public.workflowprocessreferencedoc;

CREATE TABLE IF NOT EXISTS public.workflowprocessreferencedoc
(
    uuid uuid NOT NULL DEFAULT gen_random_uuid(),
    workflowreference_id integer,
    bitstream uuid,
    workflowprocess uuid,
    workflowprocessreferencedoctype character varying COLLATE pg_catalog."default",
    CONSTRAINT workflowprocessreferencedoc_pkey PRIMARY KEY (uuid)
)

TABLESPACE pg_default;

ALTER TABLE IF EXISTS public.workflowprocessreferencedoc
    OWNER to dspace;
    
    -- Table: public.workflowprocesssenderdiary

-- DROP TABLE IF EXISTS public.workflowprocesssenderdiary;

CREATE TABLE IF NOT EXISTS public.workflowprocesssenderdiary
(
    uuid uuid NOT NULL DEFAULT gen_random_uuid(),
    workflowprocesssenderdiary_id integer,
    name character varying COLLATE pg_catalog."default",
    designation character varying COLLATE pg_catalog."default",
    contactnumber character varying COLLATE pg_catalog."default",
    email character varying COLLATE pg_catalog."default",
    organization character varying COLLATE pg_catalog."default",
    address character varying COLLATE pg_catalog."default",
    city character varying COLLATE pg_catalog."default",
    country character varying COLLATE pg_catalog."default",
    CONSTRAINT workflowprocesssenderdiary_pkey PRIMARY KEY (uuid)
)

TABLESPACE pg_default;

ALTER TABLE IF EXISTS public.workflowprocesssenderdiary
    OWNER to dspace;

-----------------------------------------------------------ashvin add
CREATE TABLE IF NOT EXISTS public.workflowprocessinwarddetails
(
    uuid uuid NOT NULL,
    workflowprocessinwarddetails_id integer,
    inwardnumber character varying COLLATE pg_catalog."default",
    inwarddate timestamp with time zone,
    receiveddate timestamp with time zone,
    CONSTRAINT workflowprocessinwarddetails_pkey PRIMARY KEY (uuid)
)

TABLESPACE pg_default;

ALTER TABLE IF EXISTS public.workflowprocessinwarddetails
    OWNER to postgres;


    CREATE TABLE IF NOT EXISTS public.workflowprocessoutwarddetails
    (
        uuid uuid NOT NULL,
        workflowprocessoutwarddetails_id integer,
        outwardnumber character varying COLLATE pg_catalog."default",
        outwarddate timestamp with time zone,
        outwardmedium_id uuid,
        outwarddepartment_id uuid,
        outwardmode_id uuid,
        CONSTRAINT workflowprocessoutwarddetails_pkey PRIMARY KEY (uuid)
    )

    TABLESPACE pg_default;

    ALTER TABLE IF EXISTS public.workflowprocessoutwarddetails
        OWNER to postgres;


        CREATE TABLE IF NOT EXISTS public.event
        (
            title character varying COLLATE pg_catalog."default",
            action_date timestamp with time zone NOT NULL,
            uuid uuid NOT NULL,
            dspaceobjectid uuid,
            action integer,
            dspaceobjecttype integer NOT NULL,
            parencollection uuid,
            parencommunity uuid,
            geolocationeventid uuid,
            event_id integer,
            userid uuid,
            documenttype integer,
            CONSTRAINT event_pkey PRIMARY KEY (uuid, action_date, dspaceobjecttype)
        )

        TABLESPACE pg_default;

        ALTER TABLE IF EXISTS public.event
            OWNER to postgres;