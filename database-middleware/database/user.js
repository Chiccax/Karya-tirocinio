/* jshint indent: 2 */

const Sequelize = require('sequelize');
module.exports = function(sequelize, DataTypes) {
  return sequelize.define('user', {
    email: {
      type: DataTypes.STRING(200),
      allowNull: false,
      primaryKey: true
    },
    telegramId: {
      type: DataTypes.STRING(20),
      allowNull: true,
      unique: "telegramId"
    },
    name: {
      type: DataTypes.STRING(50),
      allowNull: false
    },
    surname: {
      type: DataTypes.STRING(50),
      allowNull: false
    },
    password: {
      type: DataTypes.STRING(255),
      allowNull: false
    },
    phone_number: {
      type: DataTypes.STRING(50),
      allowNull: false
    },
    statements: {
      type: DataTypes.TINYINT,
      allowNull: false
    }
  }, {
    sequelize,
    tableName: 'user',
    timestamps: false,
    indexes: [
      {
        name: "PRIMARY",
        unique: true,
        using: "BTREE",
        fields: [
          { name: "email" },
        ]
      },
      {
        name: "telegramId",
        unique: true,
        using: "BTREE",
        fields: [
          { name: "telegramId" },
        ]
      },
    ]
  });
};
