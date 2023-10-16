package api;

import io.restassured.http.ContentType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

import static io.restassured.RestAssured.given;

public class ReqresTest {
    private static final String URL = "https://reqres.in";

    @Test
    public void checkAvatarAndIdTest(){
        Specifications.installSpecification(Specifications.requestSpec(URL), Specifications.responseSpec200());

        List<UserData> users = given()
                .when()
                .get("/api/users?page=2")
                .then().log().all()
                .extract().body().jsonPath().getList("data", UserData.class);

        users.forEach(i -> Assertions.assertTrue(i.getAvatar().contains(i.getId().toString())));
        users.forEach(i -> Assertions.assertTrue(i.getEmail().endsWith("@reqres.in")));
    }
}
