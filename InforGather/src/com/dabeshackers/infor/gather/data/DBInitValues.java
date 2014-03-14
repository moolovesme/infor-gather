package com.dabeshackers.infor.gather.data;

import java.util.ArrayList;
import java.util.List;

public class DBInitValues {
	public static List<String> getInsertSql() {
		List<String> sql = new ArrayList<String>();

		//		long now = new Date().getTime();
		//		String userId = "{ECC8333C-2752-4C5F-AF41-BB52D9FB62E9}";

		//		sql.add("INSERT INTO userlocation (id,user_id,lat,lng,created,updated,version) VALUES ('{afd7b2ea-f834-4c21-9aa2-1945f2d05512}','{79B5B9A6-362D-FF25-15B0-8EEBF01B1085}','14.5502232','121.0519017','1358418963316','1358418963316','1')");
		//		sql.add("INSERT INTO userlocation (id,user_id,lat,lng,created,updated,version) VALUES ('{0282a94f-eb27-4eda-88c0-76bd73ffd729}','{79B5B9A6-362D-FF25-15B0-8EEBF01B1085}','14.5502448','121.0518884','1358419276987','1358419276987','1')");
		//		sql.add("INSERT INTO userlocation (id,user_id,lat,lng,created,updated,version) VALUES ('{5b5f4ed2-8a6a-4d2a-abad-c4c33679aa59}','{79B5B9A6-362D-FF25-15B0-8EEBF01B1085}','14.5502448','121.0518884','1358419276987','1358419276987','1')");
		//		
		//		sql.add("INSERT INTO userlocation (id,user_id,lat,lng,created,updated,version) VALUES ('{fc4d1e2c-f854-424a-8663-541063d5557a}','{969E462F-3DDE-8887-DBA8-9BFE59B10D21}','14.6379782','121.02557864','1358838501000','1358838501000','1')");
		//		sql.add("INSERT INTO userlocation (id,user_id,lat,lng,created,updated,version) VALUES ('{5c69f2d8-851e-4658-89a9-173390a956ba}','{969E462F-3DDE-8887-DBA8-9BFE59B10D21}','14.63770357','121.02541287','1358841790000','1358841790000','1')");
		//		sql.add("INSERT INTO userlocation (id,user_id,lat,lng,created,updated,version) VALUES ('{54ee5b13-8949-4ab5-9544-1eea77737c2b}','{969E462F-3DDE-8887-DBA8-9BFE59B10D21}','14.63781674','121.02541211','1358920537000','1358920537000','1')");
		//		
		//		sql.add("INSERT INTO userlocation (id,user_id,lat,lng,created,updated,version) VALUES ('{5e6ff771-b4a9-41c9-8ae9-78d18f35f6f8}','{8470BAD0-7AF1-DDC2-B8D1-EEBA48841947}','14.533815','121.0574866','1358939188093','1358939188093','1')");
		//		sql.add("INSERT INTO userlocation (id,user_id,lat,lng,created,updated,version) VALUES ('{ff54067b-4d5b-4515-a2c3-d676e7ecbbbb}','{8470BAD0-7AF1-DDC2-B8D1-EEBA48841947}','14.5328958','121.0575164','1358939306657','1358939306657','1')");
		//		sql.add("INSERT INTO userlocation (id,user_id,lat,lng,created,updated,version) VALUES ('{c23ab0d0-89bb-48c1-80b8-522ed4d13e2a}','{8470BAD0-7AF1-DDC2-B8D1-EEBA48841947}','14.5275703','121.0882754','1358940287807','1358940287807','1')");
		//		sql.add("INSERT INTO userlocation (id,user_id,lat,lng,created,updated,version) VALUES ('{49af4e77-a525-43f2-a7ac-7b2d79699d33}','{8470BAD0-7AF1-DDC2-B8D1-EEBA48841947}','14.5761221','121.1921863','1358947538936','1358947538936','1')");

		sql.add("INSERT INTO " + "user" + " " + "(" + "id" + ",email" + ", password" + ", firstname" + ", lastname" + ") " + "VALUES (" + "'{553467c2-573a-40f4-a850-15b0026f9233}'" + ",'admin'" + ",'0cc175b9c0f1b6a831c399e269772661'" + //a
				",'The'" + ",'Admin'" + ")");
		//		
		//		sql.add(
		//				"INSERT INTO " + "useridentity" + " " + "(" +
		//						"id" +
		//						",username" +
		//						", password" +
		//						", role" +
		//						", firstname" +
		//						", lastname" +
		//						") " +
		//						"VALUES (" + 
		//						"'"+ GUID.createGUID() +"'" +
		//						",'agentlove'" +
		//						",'87b74cc9d891604a005490b852dda556'" + //agentlove
		//						"," + UserIdentity.USER_TYPE_AGENT + "" +
		//						",'Agent'" +
		//						",'Love'" + 
		//						")"
		//				);
		//		
		//		sql.add(
		//				"INSERT INTO " + "useridentity" + " " + "(" +
		//						"id" +
		//						",username" +
		//						", password" +
		//						", role" +
		//						", firstname" +
		//						", lastname" +
		//						") " +
		//						"VALUES (" + 
		//						"'"+ GUID.createGUID() +"'" +
		//						",'outlet'" +
		//						",'b0ba1623558e169131cb513037504286'" + //outlet
		//						"," + UserIdentity.USER_TYPE_OUTLET + "" +
		//						",'Outlet'" +
		//						",'Mall Branch'" + 
		//						")"
		//				);
		//
		//		sql.add(
		//				"INSERT INTO " + "retailer" + " " + "(" +
		//						"id" +
		//						",distributor_id" +
		//						", retailer_name" +
		//						", dsp" +
		//						", area" +
		//						", email" +
		//						", mobile" +
		//						", lat" +
		//						", lng" +
		//						", edited_by" +
		//						", created" +
		//						", updated" +
		//						", version" +
		//						") " +
		//						"VALUES (" + 
		//						"'"+ GUID.createGUID() +"'" +
		//						",'"+ userId +"'" +
		//						",'ABC Sari-sari'" +
		//						",'0918 1888 8888'" +
		//						",'Metro Manila 1'" +
		//						",'sum1@example.com'" +
		//						",'0918 1888 8888'" +
		//						",14.515875" +
		//						",121.052157" +
		//						",'"+ userId +"'" +
		//						"," + now + "" +
		//						"," + now + "" +
		//						"," + 1 + "" +
		//						")"
		//				);
		//
		//		sql.add(
		//				"INSERT INTO " + "retailer" + " " + "(" +
		//						"id" +
		//						",distributor_id" +
		//						", retailer_name" +
		//						", dsp" +
		//						", area" +
		//						", email" +
		//						", mobile" +
		//						", lat" +
		//						", lng" +
		//						", edited_by" +
		//						", created" +
		//						", updated" +
		//						", version" +
		//						") " +
		//						"VALUES (" + 
		//						"'"+ GUID.createGUID() +"'" +
		//						",'"+ userId +"'" +
		//						",'DEF Tindahan ni Jeff'" +
		//						",'0918 2888 8888'" +
		//						",'Metro Manila 1'" +
		//						",'sum1@example.com'" +
		//						",'0918 2888 8888'" +
		//						",14.522023" +
		//						",121.046578" +
		//						",'"+ userId +"'" +
		//						"," + now + "" +
		//						"," + now + "" +
		//						"," + 1 + "" +
		//						")"
		//				);
		//		
		//		sql.add(
		//				"INSERT INTO " + "retailer" + " " + "(" +
		//						"id" +
		//						",distributor_id" +
		//						", retailer_name" +
		//						", dsp" +
		//						", area" +
		//						", email" +
		//						", mobile" +
		//						", lat" +
		//						", lng" +
		//						", edited_by" +
		//						", created" +
		//						", updated" +
		//						", version" +
		//						") " +
		//						"VALUES (" + 
		//						"'"+ GUID.createGUID() +"'" +
		//						",'"+ userId +"'" +
		//						",'GHI Masarap na gulay'" +
		//						",'0918 3888 8888'" +
		//						",'Metro Manila 1'" +
		//						",'sum1@example.com'" +
		//						",'0918 3888 8888'" +
		//						",14.777845" +
		//						",121.125469" +
		//						",'"+ userId +"'" +
		//						"," + now + "" +
		//						"," + now + "" +
		//						"," + 1 + "" +
		//						")"
		//				);
		//		
		//		sql.add(
		//				"INSERT INTO " + "retailer" + " " + "(" +
		//						"id" +
		//						",distributor_id" +
		//						", retailer_name" +
		//						", dsp" +
		//						", area" +
		//						", email" +
		//						", mobile" +
		//						", lat" +
		//						", lng" +
		//						", edited_by" +
		//						", created" +
		//						", updated" +
		//						", version" +
		//						") " +
		//						"VALUES (" + 
		//						"'"+ GUID.createGUID() +"'" +
		//						",'"+ userId +"'" +
		//						",'JKL Papel Tindahan ni Jeff'" +
		//						",'0918 4888 8888'" +
		//						",'Metro Manila 1'" +
		//						",'sum1@example.com'" +
		//						",'0918 4888 8888'" +
		//						",14.597846" +
		//						",121.041557" +
		//						",'"+ userId +"'" +
		//						"," + now + "" +
		//						"," + now + "" +
		//						"," + 1 + "" +
		//						")"
		//				);

		return sql;

	}

}
