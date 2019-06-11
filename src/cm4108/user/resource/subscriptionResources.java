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
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;

import cm4108.aws.util.*;
import cm4108.config.*;

import cm4108.config.Config;
import cm4108.user.model.*;

@SuppressWarnings("serial")

@Path("/subscriptions")
public class subscriptionResources {
	
@POST
@Produces(MediaType.TEXT_PLAIN)
public Response addSubscription(	@FormParam("id") String id,
		@FormParam("requestFrom") String requestFrom,
		@FormParam("requestTo") String requestTo,
		@FormParam("updatetime") long updatetime)

{
	try	{
		subscription subscription=new subscription(id,requestFrom,requestTo,updatetime);

		DynamoDBMapper mapper=DynamoDBUtil.getDBMapper(Config.REGION,Config.LOCAL_ENDPOINT);
		mapper.save(subscription);
		return Response.status(201).entity("request saved").build();
	} catch (Exception e)
	{
		e.printStackTrace();
		return Response.status(400).entity("error in saving request").build();
	}
} //end method
	
//-----------get selected user

@GET
@Produces(MediaType.APPLICATION_JSON)
@Path("/{id}")
public  requests getSelectedUser(@PathParam("id") String id)
{
DynamoDBMapper mapper=DynamoDBUtil.getDBMapper(Config.REGION,Config.LOCAL_ENDPOINT);
requests request=mapper.load(requests.class,id);

try {
	if (request==null)
		throw new WebApplicationException(404);
} catch (Exception e) {
	// TODO Auto-generated catch block
	e.printStackTrace();

}

return request;

} //end method


//delete request from the database once rejected
@Path("/{id}")
@DELETE
public Response deleteRequest(@PathParam("id") String id)
{
	
DynamoDBMapper mapper=DynamoDBUtil.getDBMapper(Config.REGION,Config.LOCAL_ENDPOINT);
requests request=mapper.load(requests.class,id);

if (request==null)
	throw new WebApplicationException(404);

mapper.delete(request);
return Response.status(200).entity(" Request deleted").build();
} //end method


//save request from the database once approved
@Path("/{id}")
@PUT
public Response approveRequest(@PathParam("id") String id)
{
	

DynamoDBMapper mapper1=DynamoDBUtil.getDBMapper(Config.REGION,Config.LOCAL_ENDPOINT);
DynamoDBMapper mapper=DynamoDBUtil.getDBMapper(Config.REGION,Config.LOCAL_ENDPOINT);
requests request=mapper.load(requests.class,id);
String from=request.getRequestFrom();
String to=request.getRequestTo();
long time=request.getUpdatetime();
subscription sub=new subscription(id,from,to,time);
mapper1.save(sub);
mapper.delete(request);

return Response.status(200).entity("Request sucssfull.you approved the request").build();
} //end method



//------------------------------------------	
//get the list of approve friends
@Path("/username/{name}")
@GET
@Produces(MediaType.APPLICATION_JSON)
public Collection<User> getAllApprovedRequests(@PathParam("name") String requestTo)
{
		
	//System.out.println("i am here is updating friends list"+requestTo);
	 //subscription s=new subscription();
	 Map<String,AttributeValue> evaluate = new HashMap<>();
	 evaluate.put(":val1", new AttributeValue().withS(requestTo));

	//Collection<subscription> subreq=null;
	
	DynamoDBMapper mapper=DynamoDBUtil.getDBMapper(Config.REGION,Config.LOCAL_ENDPOINT);
	//DynamoDBScanExpression scanExpression=new DynamoDBScanExpression();	//create scan expression
	DynamoDBScanExpression scanExpression =new DynamoDBScanExpression()
							.withFilterExpression("requestFrom=:val1")
							.withExpressionAttributeValues(evaluate);
	
	Collection<subscription> scanResult = mapper.scan(subscription.class, scanExpression);
	//getting the friends location details from the user-location database
	Collection<User> scanResult1 = new ArrayList<User>();
	//getting the friends details from the users table 
	for(subscription subs: scanResult){
		//System.out.println(subs.getRequestTo());
		String subsname=subs.getRequestTo();
		
		DynamoDBMapper mapper1=DynamoDBUtil.getDBMapper(Config.REGION,Config.LOCAL_ENDPOINT);
		User user=mapper.load(User.class,subsname);
		
		//------------------------
		scanResult1.add(user);
		}
	
	
	//getting the friends location
	return scanResult1;

} //end method

//get one users details once clicked

@Path("/requestClick/{requestFrom}")
@GET
@Produces(MediaType.APPLICATION_JSON)
public User getClickedUser(@PathParam("requestFrom") String requestTo)
{
		
	DynamoDBMapper mapper=DynamoDBUtil.getDBMapper(Config.REGION,Config.LOCAL_ENDPOINT);
	User user=mapper.load(User.class,requestTo);

	if (user==null)
		throw new WebApplicationException(404);
	return user;

} //end method














//get all the pending requests
/*
@Path("/username/{name}")
@GET
@Produces(MediaType.APPLICATION_JSON)
public Map<String, AttributeValue> getAllApprovedRequests(@PathParam("name") String requestTo)
{
	System.out.println("i am here is updating friends list"+requestTo);
	
	 Map<String,AttributeValue> evaluate = new HashMap<>();
	 evaluate.put(":val1", new AttributeValue().withS(requestTo));

	//Collection<subscription> subreq=null;
	
	DynamoDBMapper mapper=DynamoDBUtil.getDBMapper(Config.REGION,Config.LOCAL_ENDPOINT);
	DynamoDBScanExpression scanExpression=new DynamoDBScanExpression();	//create scan expression
	scanExpression.withFilterExpression("requestTo=:val1").withExpressionAttributeValues(evaluate);
	
	//subreq=mapper.scan(subscription.class, scanExpression);
	return evaluate;
} //end method
*/



}
