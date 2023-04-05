package com.infomedia.hikvisiondemo.util.hikcentral.openapi;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.infomedia.hikvisiondemo.util.hikcentral.openapi.model.*;
import com.infomedia.hikvisiondemo.util.hikcentral.openapi.request.*;
import com.infomedia.hikvisiondemo.util.hikcentral.openapi.response.AddResponse;
import com.infomedia.hikvisiondemo.util.hikcentral.openapi.response.GetResponse;
import com.infomedia.hikvisiondemo.util.hikcentral.openapi.response.ListResponse;
import com.infomedia.hikvisiondemo.util.hikcentral.openapi.response.NormalResponse;
import lombok.SneakyThrows;
import okhttp3.*;
import okhttp3.logging.HttpLoggingInterceptor;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;

public class HikcentralOpenAPI {

    private OkHttpClient client;
    private String key;
    private String secret;
    private String url;
    private ObjectMapper objectMapper;

    private static final String PARAM_PAGE_NO = "pageNo";
    private static final String PARAM_PAGE_SIZE = "pageSize";
    private static final String PARAM_PERSON_ID = "personId";


    public HikcentralOpenAPI(String url, String key, String secret){
        this.url = url;
        this.key = key;
        this.secret = secret;

        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BASIC);

        client = allTrustingBuilder()
                .addInterceptor(chain -> {
                    Request request = chain.request();

                    String timestamp = String.valueOf(new Date().getTime());
                    String contentType = null;
                    if(request.body()!=null&&request.body().contentType()!=null){
                        contentType = request.body().contentType().toString();
                    }
                    String signature = calculateSignature(request.url().url().toString(), timestamp, contentType, request.method());
                    Request newRequest = request.newBuilder()
                            .addHeader("Accept", "*/*")
                            .addHeader("X-Ca-Key", key)
                            .addHeader("X-Ca-Signature", signature)
                            .addHeader("X-Ca-Signature-Headers", "x-ca-key,x-ca-timestamp")
                            .addHeader("X-Ca-Timestamp", timestamp)
                            .build();
                    return chain.proceed(newRequest);
                }).addInterceptor(logging).build();
    }

    private String calculateSignature(String url, String timestamp, String contentType, String method) throws IOException {
        try {
            String api = url.substring(url.indexOf("/artemis"));
            StringBuilder stringToSign = new StringBuilder();
            stringToSign.append(method.toUpperCase()).append("\n");
            stringToSign.append("*/*\n");
            if(contentType!=null){
                stringToSign.append(contentType).append("\n");
            }
            stringToSign.append("x-ca-key:").append(key).append("\n");
            stringToSign.append("x-ca-timestamp:").append(timestamp).append("\n");
            stringToSign.append(api);

            Mac hmacSha256 = Mac.getInstance("HmacSHA256");
            byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
            hmacSha256.init(new SecretKeySpec(keyBytes, 0, keyBytes.length, "HmacSHA256"));
            return new String(Base64.getEncoder().encode(
                    hmacSha256.doFinal(stringToSign.toString().getBytes(StandardCharsets.UTF_8))), StandardCharsets.UTF_8);
        }catch (Exception e){
            throw new IOException(e);
        }
    }

    @SneakyThrows
    private OkHttpClient.Builder allTrustingBuilder() {
        final TrustManager[] trustAllCerts = new TrustManager[] {
                new X509TrustManager() {
                    @Override
                    public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) {
                    }

                    @Override
                    public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) {
                    }

                    @Override
                    public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                        return new java.security.cert.X509Certificate[]{};
                    }
                }
        };

        final SSLContext sslContext = SSLContext.getInstance("SSL");
        sslContext.init(null, trustAllCerts, new java.security.SecureRandom());

        final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.sslSocketFactory(sslSocketFactory, (X509TrustManager)trustAllCerts[0]);
        builder.hostnameVerifier((hostname, session) -> true);

        return builder;
    }

    public String version() throws IOException {
        Request request = new Request.Builder()
                .url(url +"/artemis/api/common/v1/version")
                .post(RequestBody.create("", null))
                .build();
        Response response = client.newCall(request).execute();
        return response.body().string();
    }

    public ListResponse<Organization> listOrganization() throws IOException {
        final MediaType jsonType
                = MediaType.parse(org.springframework.http.MediaType.APPLICATION_JSON_VALUE);

        ObjectNode jsonNode = objectMapper.createObjectNode();
        jsonNode.put(PARAM_PAGE_NO, 1);
        jsonNode.put(PARAM_PAGE_SIZE, 500);

        RequestBody body = RequestBody.create(jsonNode.toString(), jsonType);
        Request request = new Request.Builder()
                .url(url +"/artemis/api/resource/v1/org/orgList")
                .post(body)
                .build();
        Response response = client.newCall(request).execute();
        return new ListResponse<>(response.body().string(), objectMapper, new TypeReference<>(){});
    }

    public ListResponse<PrivilegeGroup> listPrivilegeGroup(int type) throws IOException {
        final MediaType jsonType
                = MediaType.parse(org.springframework.http.MediaType.APPLICATION_JSON_VALUE);

        ObjectNode jsonNode = objectMapper.createObjectNode();
        jsonNode.put(PARAM_PAGE_NO, 1);
        jsonNode.put(PARAM_PAGE_SIZE, 500);
        jsonNode.put("type", type);

        RequestBody body = RequestBody.create(jsonNode.toString(), jsonType);
        Request request = new Request.Builder()
                .url(url +"/artemis/api/acs/v1/privilege/group")
                .post(body)
                .build();
        Response response = client.newCall(request).execute();
        return new ListResponse<>(response.body().string(), objectMapper, new TypeReference<>(){});
    }

    public ListResponse<PersonId> listPrivilegeGroupPerson(int pageNo, int pageSize, int type, String privilegeGroupId) throws IOException {
        final MediaType jsonType
                = MediaType.parse(org.springframework.http.MediaType.APPLICATION_JSON_VALUE);

        ObjectNode jsonNode = objectMapper.createObjectNode();
        jsonNode.put("privilegeGroupId", privilegeGroupId);
        jsonNode.put("type", type);
        jsonNode.put(PARAM_PAGE_NO, pageNo);
        jsonNode.put(PARAM_PAGE_SIZE, pageSize);

        RequestBody body = RequestBody.create(jsonNode.toString(), jsonType);
        Request request = new Request.Builder()
                .url(url +"/artemis/api/acs/v1/privilege/group/single/personList")
                .post(body)
                .build();
        Response response = client.newCall(request).execute();
        return new ListResponse<>(response.body().string(), objectMapper, new TypeReference<>(){});
    }

    public NormalResponse addPrivilegeGroupPersons(PrivilegeGroupPersons privilegeGroupPersons) throws IOException {
        final MediaType jsonType
                = MediaType.parse(org.springframework.http.MediaType.APPLICATION_JSON_VALUE);
        RequestBody body = RequestBody.create(objectMapper.writeValueAsString(privilegeGroupPersons), jsonType);
        Request request = new Request.Builder()
                .url(url +"/artemis/api/acs/v1/privilege/group/single/addPersons")
                .post(body)
                .build();
        Response response = client.newCall(request).execute();
        return new NormalResponse(response.body().string(), objectMapper);
    }

    public NormalResponse deletePrivilegeGroupPersons(PrivilegeGroupPersons privilegeGroupPersons) throws IOException {
        final MediaType jsonType
                = MediaType.parse(org.springframework.http.MediaType.APPLICATION_JSON_VALUE);
        RequestBody body = RequestBody.create(objectMapper.writeValueAsString(privilegeGroupPersons), jsonType);
        Request request = new Request.Builder()
                .url(url +"/artemis/api/acs/v1/privilege/group/single/deletePersons")
                .post(body)
                .build();
        Response response = client.newCall(request).execute();
        return new NormalResponse(response.body().string(), objectMapper);
    }

    public ListResponse<Person> listPerson(int pageNo, int pageSize) throws IOException {
        final MediaType jsonType
                = MediaType.parse(org.springframework.http.MediaType.APPLICATION_JSON_VALUE);

        ObjectNode jsonNode = objectMapper.createObjectNode();
        jsonNode.put(PARAM_PAGE_NO, pageNo);
        jsonNode.put(PARAM_PAGE_SIZE, pageSize);

        RequestBody body = RequestBody.create(jsonNode.toString(), jsonType);
        Request request = new Request.Builder()
                .url(url +"/artemis/api/resource/v1/person/personList")
                .post(body)
                .build();
        Response response = client.newCall(request).execute();
        return new ListResponse<>(response.body().string(), objectMapper, new TypeReference<>(){});
    }

    public GetResponse<Person> getPerson(String personId) throws IOException {
        final MediaType jsonType
                = MediaType.parse(org.springframework.http.MediaType.APPLICATION_JSON_VALUE);

        ObjectNode jsonNode = objectMapper.createObjectNode();
        jsonNode.put(PARAM_PERSON_ID, personId);

        RequestBody body = RequestBody.create(jsonNode.toString(), jsonType);
        Request request = new Request.Builder()
                .url(url +"/artemis/api/resource/v1/person/personId/personInfo")
                .post(body)
                .build();
        Response response = client.newCall(request).execute();
        return new GetResponse<>(response.body().string(), objectMapper, Person.class);
    }

    public NormalResponse deletePerson(String personId) throws IOException {
        final MediaType jsonType
                = MediaType.parse(org.springframework.http.MediaType.APPLICATION_JSON_VALUE);

        ObjectNode jsonNode = objectMapper.createObjectNode();
        jsonNode.put(PARAM_PERSON_ID, personId);

        RequestBody body = RequestBody.create(jsonNode.toString(), jsonType);
        Request request = new Request.Builder()
                .url(url +"/artemis/api/resource/v1/person/single/delete")
                .post(body)
                .build();
        Response response = client.newCall(request).execute();
        return new NormalResponse(response.body().string(), objectMapper);
    }

    public AddResponse addPerson(AddPerson person) throws IOException {
        final MediaType jsonType
                = MediaType.parse(org.springframework.http.MediaType.APPLICATION_JSON_VALUE);
        RequestBody body = RequestBody.create(objectMapper.writeValueAsString(person), jsonType);
        Request request = new Request.Builder()
                .url(url +"/artemis/api/resource/v1/person/single/add")
                .post(body)
                .build();
        Response response = client.newCall(request).execute();
        return new AddResponse(response.body().string(), objectMapper);
    }

    public NormalResponse updatePerson(UpdatePerson person) throws IOException {
        final MediaType jsonType
                = MediaType.parse(org.springframework.http.MediaType.APPLICATION_JSON_VALUE);
        RequestBody body = RequestBody.create(objectMapper.writeValueAsString(person), jsonType);
        Request request = new Request.Builder()
                .url(url +"/artemis/api/resource/v1/person/single/update")
                .post(body)
                .build();
        Response response = client.newCall(request).execute();
        return new NormalResponse(response.body().string(), objectMapper);
    }

    public NormalResponse updatePersonFaceData(UpdatePersonFaceData personFaceData) throws IOException {
        final MediaType jsonType
                = MediaType.parse(org.springframework.http.MediaType.APPLICATION_JSON_VALUE);

        RequestBody body = RequestBody.create(objectMapper.writeValueAsString(personFaceData), jsonType);
        Request request = new Request.Builder()
                .url(url +"/artemis/api/resource/v1/person/face/update")
                .post(body)
                .build();
        Response response = client.newCall(request).execute();
        return new NormalResponse(response.body().string(), objectMapper);
    }

    public GetResponse<String> getPersonFaceData(String personId, String picUri) throws IOException {
        final MediaType jsonType
                = MediaType.parse(org.springframework.http.MediaType.APPLICATION_JSON_VALUE);

        ObjectNode jsonNode = objectMapper.createObjectNode();
        jsonNode.put(PARAM_PERSON_ID, personId);
        jsonNode.put("picUri", picUri);

        RequestBody body = RequestBody.create(jsonNode.toString(), jsonType);
        Request request = new Request.Builder()
                .url(url +"/artemis/api/resource/v1/person/picture_data")
                .post(body)
                .build();
        Response response = client.newCall(request).execute();

        String responseStr = response.body().string();

        if(responseStr.contains("base64,")){
            return new GetResponse<>(true, "Success", responseStr.split("base64,")[1], 0);
        }else{
            return new GetResponse<>( false, responseStr, null, -1);
        }
    }

    public ListResponse<FaceComparisonGroup> listFaceComparisonGroup() throws IOException {
        final MediaType jsonType
                = MediaType.parse("application/json");

        ObjectNode jsonNode = objectMapper.createObjectNode();
        jsonNode.put(PARAM_PAGE_NO, 1);
        jsonNode.put(PARAM_PAGE_SIZE, 500);

        RequestBody body = RequestBody.create(jsonNode.toString(), jsonType);

        Request request = new Request.Builder()
                .url(url +"/artemis/api/frs/v1/face/groupList")
                .post(body)
                .build();
        Response response = client.newCall(request).execute();
        return new ListResponse<>(response.body().string(), objectMapper, new TypeReference<>(){});
    }

    public NormalResponse addFaceComparisonGroupPerson(AddFaceComparisonGroupPerson faceComparisonGroupPerson) throws IOException {
        final MediaType jsonType
                = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(objectMapper.writeValueAsString(faceComparisonGroupPerson), jsonType);
        Request request = new Request.Builder()
                .url(url+"/artemis/api/frs/v1/face/single/addition")
                .post(body)
                .build();
        Response response = client.newCall(request).execute();
        return new NormalResponse(response.body().string(), objectMapper);
    }

    public NormalResponse faceComparisonGroupReapplication(String faceComparisonGroupId) throws IOException {
        final MediaType jsonType
                = MediaType.parse("application/json");

        ObjectNode jsonNode = objectMapper.createObjectNode();
        jsonNode.put("indexCode", faceComparisonGroupId);

        RequestBody body = RequestBody.create(jsonNode.toString(), jsonType);

        Request request = new Request.Builder()
                .url(url+"/artemis/api/frs/v1/plan/recognition/black/restart")
                .post(body)
                .build();
        Response response = client.newCall(request).execute();
        return new NormalResponse(response.body().string(), objectMapper);
    }

    public GetResponse<Person> getPersonByCode(String personCode) throws IOException {
        final MediaType jsonType
                = MediaType.parse(org.springframework.http.MediaType.APPLICATION_JSON_VALUE);

        ObjectNode jsonNode = objectMapper.createObjectNode();
        jsonNode.put("personCode", personCode);

        RequestBody body = RequestBody.create(jsonNode.toString(), jsonType);
        Request request = new Request.Builder()
                .url(url+"/artemis/api/resource/v1/person/personCode/personInfo")
                .post(body)
                .build();
        Response response = client.newCall(request).execute();
        return new GetResponse<>(response.body().string(), objectMapper, Person.class);
    }

    public NormalResponse faceCheck(String faceData, String acsDevIndexCode) throws IOException {
        final MediaType jsonType
                = MediaType.parse("application/json");

        ObjectNode jsonNode = objectMapper.createObjectNode();
        jsonNode.put("faceData", faceData);
        jsonNode.put("acsDevIndexCode", acsDevIndexCode);

        RequestBody body = RequestBody.create(jsonNode.toString(), jsonType);

        Request request = new Request.Builder()
                .url(url+"/artemis/api/acs/v1/faceCheck")
                .post(body)
                .build();
        Response response = client.newCall(request).execute();
        return new NormalResponse(response.body().string(), objectMapper);
    }

    public NormalResponse eventSubscriptionByEventTypes(EventSubscriptionRequest eventSubscriptionRequest) throws IOException {
        final MediaType jsonType
                = MediaType.parse(org.springframework.http.MediaType.APPLICATION_JSON_VALUE);
        ObjectNode jsonNode = objectMapper.createObjectNode();

        ArrayNode arrayNode = objectMapper.createArrayNode();
        for(Integer ev : eventSubscriptionRequest.getEventTypes()){
            arrayNode.add(ev);
        }
        jsonNode.put("eventTypes", arrayNode);
        jsonNode.put("eventDest", eventSubscriptionRequest.getDestUrl());

        RequestBody body = RequestBody.create(jsonNode.toString(), jsonType);
        Request request = new Request.Builder()
                .url(url+"/artemis/api/eventService/v1/eventSubscriptionByEventTypes")
                .post(body)
                .build();
        Response response = client.newCall(request).execute();
        return new NormalResponse(response.body().string(), objectMapper);
    }

    public NormalResponse eventUnSubscriptionByEventTypes(EventUnsubscriptionRequest eventUnsubscriptionRequest) throws IOException {
        final MediaType jsonType
                = MediaType.parse(org.springframework.http.MediaType.APPLICATION_JSON_VALUE);
        ObjectNode jsonNode = objectMapper.createObjectNode();

        ArrayNode arrayNode = objectMapper.createArrayNode();
        for(Integer ev : eventUnsubscriptionRequest.getEventTypes()){
            arrayNode.add(ev);
        }
        jsonNode.put("eventTypes", arrayNode);

        RequestBody body = RequestBody.create(jsonNode.toString(), jsonType);
        Request request = new Request.Builder()
                .url(url+"/artemis/api/eventService/v1/eventUnSubscriptionByEventTypes")
                .post(body)
                .build();
        Response response = client.newCall(request).execute();
        return new NormalResponse(response.body().string(), objectMapper);
    }

    public NormalResponse personAccessLevelReapplication() throws IOException {
        RequestBody body = RequestBody.create("", null);
        Request request = new Request.Builder()
                .url(url+"/artemis/api/visitor/v1/auth/reapplication")
                .post(body)
                .build();
        Response response = client.newCall(request).execute();
        return new NormalResponse(response.body().string(), objectMapper);
    }

    public GetResponse<EventSubscription> eventSubscriptionView() throws IOException {
        RequestBody body = RequestBody.create("", null);
        Request request = new Request.Builder()
                .url(url+"/artemis/api/eventService/v1/eventSubscriptionView")
                .post(body)
                .build();
        Response response = client.newCall(request).execute();
        return new GetResponse<>(response.body().string(), objectMapper, EventSubscription.class);
    }

    public String getEventImageData(String imageUri) throws IOException {
        final MediaType jsonType
                = MediaType.parse(org.springframework.http.MediaType.APPLICATION_JSON_VALUE);
        ObjectNode jsonNode = objectMapper.createObjectNode();
        jsonNode.put("picUri", imageUri);
        RequestBody body = RequestBody.create(jsonNode.toString(), jsonType);
        Request request = new Request.Builder()
                .url(url+"/artemis/api/eventService/v1/image_data")
                .post(body)
                .build();
        Response response = client.newCall(request).execute();
        return response.body().string();
    }

    public ListResponse<Door> listDoor() throws IOException {
        final MediaType jsonType
                = MediaType.parse(org.springframework.http.MediaType.APPLICATION_JSON_VALUE);

        ObjectNode jsonNode = objectMapper.createObjectNode();
        jsonNode.put(PARAM_PAGE_NO, 1);
        jsonNode.put(PARAM_PAGE_SIZE, 500);

        RequestBody body = RequestBody.create(jsonNode.toString(), jsonType);

        Request request = new Request.Builder()
                .url(url+"/artemis/api/resource/v1/acsDoor/acsDoorList")
                .post(body)
                .build();
        Response response = client.newCall(request).execute();
        return new ListResponse<>(response.body().string(), objectMapper, new TypeReference<>(){});
    }

    public GetResponse<Camera> getCamera(String cameraId) throws IOException {
        final MediaType jsonType
                = MediaType.parse(org.springframework.http.MediaType.APPLICATION_JSON_VALUE);

        ObjectNode jsonNode = objectMapper.createObjectNode();
        jsonNode.put("cameraIndexCode", cameraId);

        RequestBody body = RequestBody.create(jsonNode.toString(), jsonType);
        Request request = new Request.Builder()
                .url(url +"/artemis/api/resource/v1/cameras/indexCode")
                .post(body)
                .build();
        Response response = client.newCall(request).execute();
        return new GetResponse<>(response.body().string(), objectMapper, Camera.class);
    }

    public GetResponse<Door> getDoor(String doorId) throws IOException {
        final MediaType jsonType
                = MediaType.parse(org.springframework.http.MediaType.APPLICATION_JSON_VALUE);

        ObjectNode jsonNode = objectMapper.createObjectNode();
        jsonNode.put("doorIndexCode", doorId);

        RequestBody body = RequestBody.create(jsonNode.toString(), jsonType);
        Request request = new Request.Builder()
                .url(url +"/artemis/api/resource/v1/acsDoor/indexCode/acsDoorInfo")
                .post(body)
                .build();
        Response response = client.newCall(request).execute();
        return new GetResponse<>(response.body().string(), objectMapper, Door.class);
    }

    public ListResponse<Camera> listCamera() throws IOException {
        final MediaType jsonType
                = MediaType.parse(org.springframework.http.MediaType.APPLICATION_JSON_VALUE);

        ObjectNode jsonNode = objectMapper.createObjectNode();
        jsonNode.put(PARAM_PAGE_NO, 1);
        jsonNode.put(PARAM_PAGE_SIZE, 500);

        RequestBody body = RequestBody.create(jsonNode.toString(), jsonType);

        Request request = new Request.Builder()
                .url(url+"/artemis/api/resource/v1/camera/advance/cameraList")
                .post(body)
                .build();
        Response response = client.newCall(request).execute();
        return new ListResponse<>(response.body().string(), objectMapper, new TypeReference<>(){});
    }

    public ListResponse<VehicleGroup> listVehicleGroup() throws IOException {
        final MediaType jsonType
                = MediaType.parse(org.springframework.http.MediaType.APPLICATION_JSON_VALUE);

        ObjectNode jsonNode = objectMapper.createObjectNode();
        jsonNode.put(PARAM_PAGE_NO, 1);
        jsonNode.put(PARAM_PAGE_SIZE, 500);

        RequestBody body = RequestBody.create(jsonNode.toString(), jsonType);

        Request request = new Request.Builder()
                .url(url+"/artemis/api/resource/v1/vehicleGroup/vehicleGroupList")
                .post(body)
                .build();
        Response response = client.newCall(request).execute();
        return new ListResponse<>(response.body().string(), objectMapper, new TypeReference<>(){});
    }

    public ListResponse<Vehicle> listVehicle(String vehicleGroupIndexCode) throws IOException {
        final MediaType jsonType
                = MediaType.parse(org.springframework.http.MediaType.APPLICATION_JSON_VALUE);

        ObjectNode jsonNode = objectMapper.createObjectNode();
        jsonNode.put(PARAM_PAGE_NO, 1);
        jsonNode.put(PARAM_PAGE_SIZE, 500);
        jsonNode.put("vehicleGroupIndexCode", vehicleGroupIndexCode);

        RequestBody body = RequestBody.create(jsonNode.toString(), jsonType);

        Request request = new Request.Builder()
                .url(url+"/artemis/api/resource/v1/vehicle/vehicleList")
                .post(body)
                .build();
        Response response = client.newCall(request).execute();
        return new ListResponse<>(response.body().string(), objectMapper, new TypeReference<>(){});
    }

    public ListResponse<Vehicle> searchVehicle(String plateNo, String vehicleGroupIndexCode) throws IOException {
        final MediaType jsonType
                = MediaType.parse(org.springframework.http.MediaType.APPLICATION_JSON_VALUE);

        ObjectNode jsonNode = objectMapper.createObjectNode();
        jsonNode.put(PARAM_PAGE_NO, 1);
        jsonNode.put(PARAM_PAGE_SIZE, 500);
        jsonNode.put("plateNo", plateNo);
        jsonNode.put("vehicleGroupIndexCode", vehicleGroupIndexCode);

        RequestBody body = RequestBody.create(jsonNode.toString(), jsonType);

        Request request = new Request.Builder()
                .url(url+"/artemis/api/resource/v1/vehicle/advance/vehicleList")
                .post(body)
                .build();
        Response response = client.newCall(request).execute();
        return new ListResponse<>(response.body().string(), objectMapper, new TypeReference<>(){});
    }

    public GetResponse<Vehicle> getVehicle(String vehicleId) throws IOException {
        final MediaType jsonType
                = MediaType.parse(org.springframework.http.MediaType.APPLICATION_JSON_VALUE);

        ObjectNode jsonNode = objectMapper.createObjectNode();
        jsonNode.put("vehicleIndexCode", vehicleId);

        RequestBody body = RequestBody.create(jsonNode.toString(), jsonType);
        Request request = new Request.Builder()
                .url(url +"/artemis/api/resource/v1/vehicle/indexCode/vehicleInfo")
                .post(body)
                .build();
        Response response = client.newCall(request).execute();
        return new GetResponse<>(response.body().string(), objectMapper, Vehicle.class);
    }

    public AddResponse addVehicle(AddVehicle vehicle) throws IOException {
        final MediaType jsonType
                = MediaType.parse(org.springframework.http.MediaType.APPLICATION_JSON_VALUE);
        RequestBody body = RequestBody.create(objectMapper.writeValueAsString(vehicle), jsonType);
        Request request = new Request.Builder()
                .url(url +"/artemis/api/resource/v1/vehicle/single/add")
                .post(body)
                .build();
        Response response = client.newCall(request).execute();
        return new AddResponse(response.body().string(), objectMapper, "vehicleId");
    }

    public NormalResponse deleteVehicle(String vehicleId) throws IOException {
        final MediaType jsonType
                = MediaType.parse(org.springframework.http.MediaType.APPLICATION_JSON_VALUE);

        ObjectNode jsonNode = objectMapper.createObjectNode();
        jsonNode.put("vehicleId", vehicleId);

        RequestBody body = RequestBody.create(jsonNode.toString(), jsonType);
        Request request = new Request.Builder()
                .url(url +"/artemis/api/resource/v1/vehicle/single/delete")
                .post(body)
                .build();
        Response response = client.newCall(request).execute();
        return new NormalResponse(response.body().string(), objectMapper);
    }

    public NormalResponse updateVehicle(UpdateVehicle person) throws IOException {
        final MediaType jsonType
                = MediaType.parse(org.springframework.http.MediaType.APPLICATION_JSON_VALUE);
        RequestBody body = RequestBody.create(objectMapper.writeValueAsString(person), jsonType);
        Request request = new Request.Builder()
                .url(url +"/artemis/api/resource/v1/vehicle/single/update")
                .post(body)
                .build();
        Response response = client.newCall(request).execute();
        return new NormalResponse(response.body().string(), objectMapper);
    }

}
