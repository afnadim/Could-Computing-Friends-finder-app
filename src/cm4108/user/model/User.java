package cm4108.user.model;
//Author: S M ABDULLAH FERDOUS
//Cloud computing coursework

import com.amazonaws.services.dynamodbv2.datamodeling.*;

import cm4108.config.*;

@DynamoDBTable(tableName="user-location")
public class User
{
private  String name;
private  double longitude,latitude;
private  long updatetime;

public User()
{
} //end method



public User(String name,double longitude,double latitude,long updatetime)
{
this.setName(name);
this.setLongitude(longitude);
this.setLatitude(latitude);
this.setUpdatetime(updatetime);
} //end method

@DynamoDBHashKey(attributeName="username")
public String getName() {
	return name;
} //end method

public void setName(String name) {
	this.name = name;
} //end method

@DynamoDBAttribute(attributeName="longitude")
public double getLongitude() {
	return longitude;
} //end method

public void setLongitude(double longitude) {
	this.longitude = longitude;
} //end method

@DynamoDBAttribute(attributeName="latitude")
public double getLatitude() {
	return latitude;
} //end method

public void setLatitude(double latitude) {
	this.latitude = latitude;
} //end method

@DynamoDBAttribute(attributeName="Timestamp")
public long getUpdatetime() {
	return updatetime;
}

public void setUpdatetime(long updatetime) {
	this.updatetime = updatetime;
}
} //end class
