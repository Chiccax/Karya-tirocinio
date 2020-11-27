var DataTypes = require("sequelize").DataTypes;
var _fire = require("./fire");
var _user = require("./user");

function initModels(sequelize) {
  var fire = _fire(sequelize, DataTypes);
  var user = _user(sequelize, DataTypes);

  fire.belongsTo(user, { foreignKey: "user_email"});
  user.hasMany(fire, { foreignKey: "user_email"});

  return {
    fire,
    user,
  };
}
module.exports = initModels;
module.exports.initModels = initModels;
module.exports.default = initModels;
