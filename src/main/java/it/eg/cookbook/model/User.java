package it.eg.cookbook.model;

import io.swagger.annotations.Api;
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

    @ApiModelProperty(notes= "sambaLMPassword", position = 7, example = "password")
    private String sambaLMPassword;

    @ApiModelProperty(notes= "sambaNTPassword", position = 8, example = "password")
    private String sambaNTPassword;

    @ApiModelProperty(notes= "sambaSID", position = 9, example = "S-1-5-21-1288326302-1102467403-3443272390-3000")
    private String sambaSID;

    @ApiModelProperty(notes= "sambaAcctFlags", position = 10, example = "U")
    private String sambaAcctFlags;

    @ApiModelProperty(notes= "uidNumber", position = 11, example = "1000")
    private String uidNumber;

    @ApiModelProperty(notes= "homeDirectory", position = 12, example = "/home/users/sragnetti")
    private String homeDirectory;

    @ApiModelProperty(notes= "loginShell", position = 13, example = "/bin/bash")
    private String loginShell;


}
