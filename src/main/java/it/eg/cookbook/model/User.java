package it.eg.cookbook.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel
public class User {

   // public void User(){}

    @ApiModelProperty(notes = "Common Name", position = 1, required = true, example = "sragnetti")
    private String cn;

    @ApiModelProperty(notes = "Name", position = 2, example = "sara")
    private String givenName;

    @ApiModelProperty(notes = "Surname", position = 3, required = true, example = "ragnetti")
    private String sn;

    @ApiModelProperty(notes = "e-mail", position = 4, example = "sragnetti@imolinfo.it")
    private String email;

    @ApiModelProperty(notes = "user password", position = 5,example = "password")
    private String password;

    @ApiModelProperty(notes = "uid", position = 6, example = "sragnetti")
    private String uid;
}
