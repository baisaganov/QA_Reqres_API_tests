package api;

import api.colors.ColorData;
import api.registration.Register;
import api.registration.SuccessReg;
import api.registration.UnSuccessReg;
import api.users.UserData;
import api.users.UserTime;
import api.users.UserTimeResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.util.List;

import static io.restassured.RestAssured.given;

public class ReqresPOJOTest {
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

    @Test
    public void successRegTest(){
        Specifications.installSpecification(Specifications.requestSpec(URL), Specifications.responseSpec200());

        Integer id = 4;
        String token = "QpwL5tke4Pnpja7X4";

        Register user = new Register("eve.holt@reqres.in", "pistol");
        SuccessReg successReg = given()
                .body(user)
                .when()
                .post("/api/register")
                .then().log().all()
                .extract().as(SuccessReg.class);

        Assertions.assertNotNull(successReg.getId());
        Assertions.assertNotNull(successReg.getToken());

        Assertions.assertEquals(id, successReg.getId());
        Assertions.assertEquals(token, successReg.getToken());
    }

    @Test
    public void unSuccessRegTest(){
        Specifications.installSpecification(Specifications.requestSpec(URL), Specifications.responseSpec400());

        Register user = new Register("sydney@fife", "");

        UnSuccessReg successReg = given()
                .body(user)
                .when()
                .post("/api/register")
                .then().log().all()
                .extract().as(UnSuccessReg.class);

        Assertions.assertEquals("Missing password", successReg.getError());

    }

    @Test
    public void sortedYearsTest(){
        Specifications.installSpecification(Specifications.requestSpec(URL), Specifications.responseSpec200());

        List<ColorData> colors = given()
                .when()
                .get("/api/unknown")
                .then().log().all()
                .extract().body().jsonPath().getList("data", ColorData.class);

        List<Integer> years = colors.stream().map(ColorData::getYear).toList();
        List<Integer> sortedYearsList = years.stream().sorted().toList();

        Assertions.assertEquals(sortedYearsList, years);
    }

    @Test
    public void deleteUserTest(){
        Specifications.installSpecification(Specifications.requestSpec(URL), Specifications.responseSpecUnique(204));
        given()
                .when()
                .delete("/api/users/2")
                .then().log().all();
    }

    @Test
    public void timeTest(){
        Specifications.installSpecification(Specifications.requestSpec(URL), Specifications.responseSpecUnique(200));

        UserTime userTime = new UserTime("morpheus", "zion resident");
        UserTimeResponse response = given()
                .body(userTime)
                .when()
                .put("/api/users/2")
                .then().log().all()
                .extract().as(UserTimeResponse.class);
        String currentTime = Clock.systemUTC().instant().toString().replaceAll("(.{8})$", "");
        Assertions.assertEquals(currentTime, response.getUpdatedAt().replaceAll("(.{5})$", ""));

    }

}
