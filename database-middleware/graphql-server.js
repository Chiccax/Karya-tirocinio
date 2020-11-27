const express = require('express');
const { ApolloServer, gql } = require('apollo-server-express');
const db = require('./database/db');
const { Op } = require("sequelize");

const typeDefs = gql`
  type User {
      email: String
      name: String
      surname: String
      password: String
      phone_number: String
      statements: Boolean
  }

  type Fire {
    id: Int
    actual_location: String
    fire_location: String
    photo: String
    industrial_area: Boolean
    waste_dump: Boolean
    city_center: Boolean
    trusses: Boolean
    gas_pipeline: Boolean
    bell_tower: Boolean
    more_info: String
    dateAndTime: String
    user_email: String
  }

  type Query {
    getAllUsers: [User]
    getUserById(email: String): User
    getUserByIdAndPassword(email: String, password: String): User
    getFireByEmail(email: String): [Fire]
    getFireByTimeDateAndLocation(dateAndTime: String, fire_location: String): [Fire]
    getAllFireLocation: [String]
  }

  type Mutation {
      createUser(email: String, name: String, surname: String, password: String, phone_number: String, statements: Boolean): User
      updateUserEmail(userToUpdate: String, email: String): User
      updateUserName(userToUpdate: String, name: String): User
      updateUserSurname(userToUpdate: String, surname: String): User
      updateUserPassword(userToUpdate: String, password: String): User
      updateUserPhoneNumber(userToUpdate: String, phone_number: String): User
      deleteUser(userToDelete: String): User 
      createFire(actual_location: String, fire_location: String, photo: String, industrial_area: Boolean, waste_dump: Boolean, city_center: Boolean, trusses: Boolean, gas_pipeline: Boolean, bell_tower: Boolean, more_info: String, dateAndTime: String, email: String): Fire
    }
`;

const resolvers = {
  Query: {
    getAllUsers: async () => db.user.findAll(),
    getUserById: async (obj, args, context, info) => db.user.findByPk(args.email),
    getUserByIdAndPassword: async (obj, args, context, info) => {
      const result = await db.user.findOne({where: {email: args.email, password: args.password}})
      return result
    },
    getFireByEmail: async (obj, args, context, info) => {
      const result = await db.fire.findAll({where: {user_email: args.email}})
      return result
    },
    getFireByTimeDateAndLocation: async (obj, args, context, info) => {
      const datePar = args.dateAndTime;
      const date = datePar.substring(0, 11);
      const fireLocation = args.fire_location;
      const address = fireLocation.split(",")[0]
      const cap = fireLocation.split(",")[2]
      const result = await db.fire.findAll({
        where: {
          [Op.and]: [
            {
              fire_location: {
                [Op.like]: address+'%'+cap+'%'
              } 
            },
            {
              dateAndTime: {
                [Op.like]: date+'%'
              }
            }
          ]
        }
      })
      return result
    },
    getAllFireLocation: async(obj, args, context, info) => {
      const result = await db.fire.findAll({attributes: ['fire_location']})
      return result.map(fire => fire.dataValues.fire_location)
    } 
  },
  Mutation: {
    //User Mutation
    createUser: async (parent, args, ctx) => db.user.create({email: args.email, name: args.name, surname: args.surname, password: args.password, phone_number: args.phone_number, statements: args.statements}),
    //Update data user
    //email
    updateUserEmail: async (parent, args, ctx) => {
      await db.user.update({email: args.email},{
        where: {email: args.userToUpdate}
      });
      return await db.user.findOne({where:{email: args.email}});
    }, 
    //name
    updateUserName: async (parent, args, ctx) => {
      await db.user.update({name: args.name},{
        where: {email: args.userToUpdate}
      });
      return await db.user.findOne({where:{email: args.userToUpdate}});
    }, 
    //surname
    updateUserSurname: async (parent, args, ctx) => {
      await db.user.update({surname: args.surname},{
        where: {email: args.userToUpdate}
      });
      return await db.user.findOne({where:{email: args.userToUpdate}});
    }, 
    //password
    updateUserPassword: async (parent, args, ctx) => {
      await db.user.update({password: args.password},{
        where: {email: args.userToUpdate}
      });
      return await db.user.findOne({where:{email: args.userToUpdate}});
    }, 
    //phone_number
    updateUserPhoneNumber: async (parent, args, ctx) => {
      await db.user.update({phone_number: args.phone_number},{
        where: {email: args.userToUpdate}
      });
      return await db.user.findOne({where:{email: args.userToUpdate}});
    }, 
    deleteUser: async (parent, args, ctx) => db.user.destroy({where: {email: args.userToDelete}}),
    //Fire Mutation
    createFire: async (parent, args, ctx) => db.fire.create({actual_location: args.actual_location, fire_location: args.fire_location, photo: args.photo, industrial_area: args.industrial_area, waste_dump: args.waste_dump, city_center: args.city_center, trusses: args.trusses, gas_pipeline: args.gas_pipeline, bell_tower: args.bell_tower, more_info: args.more_info, dateAndTime: args.dateAndTime, user_email: args.email})
  }
};

const server = new ApolloServer({ typeDefs, resolvers });

const app = express();
server.applyMiddleware({ app });

app.listen({ port: 4000 }, () =>
  console.log('Now browse to http://localhost:4000' + server.graphqlPath)
);