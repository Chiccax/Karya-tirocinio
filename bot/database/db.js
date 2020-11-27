const Sequelize = require('sequelize')

var db = {}

const sequelize = new Sequelize(
    'INSERT_DB_NAME_HERE',
    'INSERT_DB_USERNAME_HERE',
    'INSERT_DB_PASSWORD_HERE',
    {
        host: 'INSERT_HOST_HERE',
        port: 'INSERT_PORT_HERE',
        dialect: 'mysql',
        define: {
            freezeTableName: true,
        },
        pool: {
            max: 5,
            min: 0,
            acquire: 30000,
            idle: 10000,
        },
        // <http://docs.sequelizejs.com/manual/tutorial/querying.html#operators>
        operatorsAliases: false,
    },
)

let models = [
    require('./fire'),
    require('./user')
]

// Initialize models
models.forEach(model => {
    const seqModel = model(sequelize, Sequelize)
    db[seqModel.name] = seqModel
})

// Apply associations
Object.keys(db).forEach(key => {
    if ('associate' in db[key]) {
        db[key].associate(db)
    }
})

db.sequelize = sequelize
db.Sequelize = Sequelize

module.exports = db