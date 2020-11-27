/* jshint indent: 2 */

const Sequelize = require('sequelize');
module.exports = function(sequelize, DataTypes) {
  return sequelize.define('fire', {
    id: {
      autoIncrement: true,
      type: DataTypes.INTEGER,
      allowNull: false,
      primaryKey: true
    },
    actual_location: {
      type: DataTypes.STRING(100),
      allowNull: false
    },
    fire_location: {
      type: DataTypes.STRING(100),
      allowNull: false
    },
    photo: {
      type: DataTypes.BLOB,
      allowNull: false
    },
    industrial_area: {
      type: DataTypes.TINYINT,
      allowNull: false
    },
    waste_dump: {
      type: DataTypes.TINYINT,
      allowNull: false
    },
    city_center: {
      type: DataTypes.TINYINT,
      allowNull: false
    },
    trusses: {
      type: DataTypes.TINYINT,
      allowNull: false
    },
    gas_pipeline: {
      type: DataTypes.TINYINT,
      allowNull: false
    },
    bell_tower: {
      type: DataTypes.TINYINT,
      allowNull: false
    },
    more_info: {
      type: DataTypes.STRING(255),
      allowNull: false
    },
    dateAndTime: {
      type: DataTypes.STRING(30),
      allowNull: false
    },
    user_email: {
      type: DataTypes.STRING(200),
      allowNull: true,
      references: {
        model: 'user',
        key: 'email'
      }
    }
  }, {
    sequelize,
    tableName: 'fire',
    timestamps: false,
    indexes: [
      {
        name: "PRIMARY",
        unique: true,
        using: "BTREE",
        fields: [
          { name: "id" },
        ]
      },
      {
        name: "user_email",
        using: "BTREE",
        fields: [
          { name: "user_email" },
        ]
      },
    ]
  });
};
