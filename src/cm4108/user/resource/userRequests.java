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
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.stepfunctions.builder.conditions.Condition;
import com.sun.research.ws.wadl.Request;

import cm4108.aws.util.*;
import cm4108.config.*;
import cm4108.user.model.*;


@SuppressWarnings("serial")

@Path("/requests")

public class userRequests {
	
	
@POST
@Produces(MediaType.TEXT_PLAIN)
public Response addRequest(	@FormParam("id") String id,
							@FormParam("requestFrom") String requestFrom,
							@FormParam("requestTo") String requestTo,
							@FormParam("updatetime") long updatetime)

{
	try	{
		requests request=new requests(requestFrom,requestTo,updatetime);

		if(requestFrom.isEmpty() || requestTo.isEmpty() ) {
			
			return Response.status(400).entity("Please enter values in the input box").build();
		}
		//------checking if there is already a pending friend request with the same form and to name
		Map<String,AttributeValue> evaluate = new HashMap<>();
		evaluate.put(":val1", new AttributeValue().withS(requestTo));
		evaluate.put(":val2", new AttributeValue().withS(requestFrom));
		DynamoDBMapper mapper=DynamoDBUtil.getDBMapper(Config.REGION,Config.LOCAL_ENDPOINT);
		//DynamoDBScanExpression scanExpression=new DynamoDBScanExpression();	//create scan expression
		DynamoDBScanExpression scanExpression =new DynamoDBScanExpression()
				.withFilterExpression("requestTo =:val1 and requestFrom = :val2 ")
				.withExpressionAttributeValues(evaluate);

		Collection<requests> scanResult = mapper.scan(requests.class, scanExpression);
		//------checking if they are already friend
		Map<String,AttributeValue> evaluate1 = new HashMap<>();
		evaluate1.put(":val3", new AttributeValue().withS(requestTo));
		evaluate1.put(":val4", new AttributeValue().withS(requestFrom));
		//DynamoDBMapper mapper1=DynamoDBUtil.getDBMapper(Config.REGION,Config.LOCAL_ENDPOINT);
		//DynamoDBScanExpression scanExpression=new DynamoDBScanExpression();	//create scan expression
		DynamoDBScanExpression scanExpression1 =new DynamoDBScanExpression()
				.withFilterExpression("requestTo =:val3 and requestFrom = :val4 ")
				.withExpressionAttributeValues(evaluate1);

		Collection<subscription> scanResult1 = mapper.scan(subscription.class, scanExpression1);
		
		
		if(scanResult.isEmpty() && scanResult1.isEmpty()) {
			mapper.save(request);
			return Response.status(201).entity("request saved").build();
		}
		else {
			
			return Response.status(400).entity("you already have a pending request or you are already friends").build();
		}
		
		
		
	} catch (Exception e)
	{
		e.printStackTrace();
		return Response.status(500).entity("error in saving request").build();
	}
	
} //end method
	
//get all the pending requests
@Path("/{name}")
@GET
@Produces(MediaType.APPLICATION_JSON)
public Collection<requests> getAllPendingRequests(@PathParam("name") String requestTo)
{
	
	
	
	 Map<String,AttributeValue> evaluate = new HashMap<>();
	 evaluate.put(":val1", new AttributeValue().withS(requestTo));

	DynamoDBMapper mapper=DynamoDBUtil.getDBMapper(Config.REGION,Config.LOCAL_ENDPOINT);
	DynamoDBScanExpression scanExpression =new DynamoDBScanExpression()
							.withFilterExpression("requestTo=:val1")
							.withExpressionAttributeValues(evaluate);
	
	Collection<requests> scanResult = mapper.scan(requests.class, scanExpression);
	return scanResult;	
} //end method
	
}//end of class
