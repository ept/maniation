function retval = bones1_leftarm()

    retval = [
        [0.000,  0.025, 0.932],   0*pi/180 % base
        [0.000,  0.025, 1.145],   0*pi/180 % spine1
        [0.000,  0.025, 1.380],   0*pi/180 % spine2
        [0.143,  0.061, 1.405],  86*pi/180 % torso.l
        [0.184,  0.061, 1.416], -14*pi/180 % shoulder.l
        [0.195,  0.040, 1.121], 177*pi/180 % upperarm.l
        [0.216, -0.031, 0.872], 181*pi/180 % lowerarm.l
        [0.221, -0.043, 0.782], 267*pi/180 % hand.l
        [0.221, -0.044, 0.694], -90*pi/180 % fingers.l
    ];

endfunction
