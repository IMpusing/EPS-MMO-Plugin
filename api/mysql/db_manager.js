const sql = require('./mysql')
const encrypt = require('../tools/encryption')

const databases = ["eps_sessions", "eps_users", "eps_regions"];
const tables = 
[
    {db_name: "eps_users", table_name: "players", constructor: "`ID` INT NOT NULL AUTO_INCREMENT , `UUID` TEXT NOT NULL , `RANK` TEXT NOT NULL , `MONEY` INT NOT NULL , PRIMARY KEY (`ID`)"},
    {db_name: "eps_users", table_name: "logins", constructor: "`ID` INT NOT NULL AUTO_INCREMENT , `USERNAME` TEXT NOT NULL , `PASSWORD` TEXT NOT NULL, PRIMARY KEY (`ID`)"},
    {db_name: "eps_users", table_name: "characters", constructor: "`ID` INT NOT NULL AUTO_INCREMENT , `UUID` TEXT NOT NULL , `STATS` TEXT NOT NULL , `LAST_POS` TEXT NOT NULL, `NAME` TEXT NOT NULL, `EXP` INT NOT NULL, `LEVEL` INT NOT NULL, PRIMARY KEY (`ID`)"},
    {db_name: "eps_regions", table_name: "regions", constructor: "`ID` INT NOT NULL AUTO_INCREMENT , `NAME` TEXT NOT NULL , `LEVEL` INT NOT NULL , PRIMARY KEY (`ID`)"},
    {db_name: "eps_regions", table_name: "cities", constructor: "`ID` INT NOT NULL AUTO_INCREMENT , `NAME` TEXT NOT NULL, `REGION_ID` INT NOT NULL , PRIMARY KEY (`ID`)"},
    {db_name: "eps_regions", table_name: "houses", constructor: "`ID` INT NOT NULL AUTO_INCREMENT , `NAME` TEXT NOT NULL , `COSTS` INT NOT NULL , `OWNER_UUID` TEXT NOT NULL , `BLOCKS_INSIDE` TEXT NOT NULL , `DOORS` TEXT NOT NULL , `SPAWN_POS` TEXT NOT NULL , `SHIELD_POS` TEXT NOT NULL , `CITY_ID` INT NOT NULL, `RENTTIME` INT NOT NULL , PRIMARY KEY (`ID`)"},
    {db_name: "eps_regions", table_name: "npc", constructor: "`ID` INT NOT NULL AUTO_INCREMENT , `NAME` TEXT NOT NULL, `SCRIPT` TEXT NOT NULL , `POS` TEXT NOT NULL, `ROTATION` TEXT NOT NULL, `SKIN` TEXT NOT NULL, PRIMARY KEY (`ID`)"},
    {db_name: "eps_sessions", table_name: "sessions", constructor: "`ID` INT NOT NULL AUTO_INCREMENT , `SESSION_ID` TEXT NOT NULL, `UUID` TEXT NOT NULL, `ACTIVE` INT NOT NULL, PRIMARY KEY (`ID`)"},
    {db_name: "eps_sessions", table_name: "web_sessions", constructor: "`ID` INT NOT NULL AUTO_INCREMENT , `SESSION_ID` TEXT NOT NULL,  `EXP_DATE` DATE NOT NULL, PRIMARY KEY (`ID`)"}
]

function init(){

    //Init all databases
    
    databases.forEach(db => {
        sql.createDatabaseIfNotExist(db).then(() => {});
    });

    //Init all tables
    
    tables.forEach(tb => {
        sql.createTablefNotExist(tb.db_name, tb.table_name, tb.constructor).then(() => {});
    });

    //Init login

    sql.query("SELECT * FROM `eps_users`.`logins`").then(((res) => {
        if(sql.isRSempty(res)){
            let username = 'admin';
            let password = 'eps';

            encrypt.cryptPassword(password, (err, res) => {
                password = res;                    
                sql.query("INSERT INTO `eps_users`.`logins` (`ID`, `USERNAME`, `PASSWORD`) VALUES (NULL, '" + username + "', '" + password + "'); ");
            })            
        }
    }))
}      

const getSession = (key) =>{
    return new Promise((resolve, reject) => {
        sql.query("SELECT * FROM `eps_sessions`.`sessions` WHERE SESSION_ID = '" + key + "';").then((ret) => {
            resolve(ret);
        });
    });
}

const getUser = (name) =>{
    return new Promise((resolve, reject) => {
        sql.query("SELECT * FROM `eps_users`.`logins` WHERE USERNAME = '" + name + "';").then((ret) => {
            resolve(ret);
        });
    });
}

const verifyUser = (name, password) =>{
    return new Promise((resolve, reject) => {
        getUser(name).then((ret) =>{
            if(sql.isRSempty(ret)){
                resolve(false);
            }else{          
                encrypt.comparePassword(password, ret[0].PASSWORD, (err, r) => {
                    if(r){
                        resolve(true)
                    }else{
                        resolve(false)
                    }   
                })
                           
            }
        })
    })    
}


module.exports = {init, getSession, getUser, verifyUser}