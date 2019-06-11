package cm4108.user.resource;
//Author: S M ABDULLAH FERDOUS
//Cloud computing coursework

//general Java
import java.util.*;
//JAX-RS

import javax.ws.rs.*;
import javax.ws.rs.core.*;

import com.amazonaws.regions.Regions;
//AWS SDK
import com.amazonaws.services.dynamodbv2.datamodeling.*;

import cm4108.aws.util.*;
import cm4108.config.*;
import cm4108.user.model.*;
@SuppressWarnings("serial")

@Path("/user")
public class UserResource
{
@POST
@Produces(MediaType.TEXT_PLAIN)
public Response addUser(	@FormParam("name") String name,
							@FormParam("longitude") double longitude,
							@FormParam("latitude") double latitude,
							@FormParam("updatetime") long updatetime)
							
{
try	{
	User user=new User(name,longitude,latitude,updatetime);
	
	DynamoDBMapper mapper=DynamoDBUtil.getDBMapper(Config.REGION,Config.LOCAL_ENDPOINT);
	mapper.save(user);
	return Response.status(201).entity("New location saved").build();
	} catch (Exception e)
		{
		e.printStackTrace();
		return Response.status(400).entity("error in saving user").build();
		}
} //end method


//response to login
@Path("/login")
@POST
@Produces(MediaType.TEXT_PLAIN)
public Response login(	@FormParam("name") String name)
		
{

try	{
		
		//if the user name is empty error 
		if (name.isEmpty()) {
			return Response.status(400).entity("error logging in the  User.No input detected").build();
		}
	} catch (Exception e)
	{
		e.printStackTrace();
		return Response.status(500).entity("error logging in the  user").build();
	}
//checking if the user already in the database
DynamoDBMapper mapper=DynamoDBUtil.getDBMapper(Config.REGION,Config.LOCAL_ENDPOINT);
User user=mapper.load(User.class,name);

if (user==null) {
	User user1=new User(name,0,0,0);
	mapper.save(user1);
	return Response.status(201).entity("new user created and logged in as "+ ": "+ name).build();
}
//existing user 
return Response.status(201).entity("Welcome back.you are  logged in as " + ": "+name).build();

} //end method


//get one specific user
@Path("/{name}")
@GET
@Produces(MediaType.APPLICATION_JSON)
public User getOneUser(@PathParam("name") String name)
{
	
System.out.println(name);
DynamoDBMapper mapper=DynamoDBUtil.getDBMapper(Config.REGION,Config.LOCAL_ENDPOINT);
User user=mapper.load(User.class,name);

if (user==null)
	throw new WebApplicationException(404);
return user;

} //end method


} //end class

