function retval = bones2_rightarm()
    
    retval = [
        [ 0.000,  0.025, 0.932],   0*pi/180 % base
        [ 0.000,  0.025, 1.145],   0*pi/180 % spine1
        [ 0.000,  0.025, 1.380],   0*pi/180 % spine2
        [-0.143,  0.061, 1.406], -86*pi/180 % torso.r
        [-0.183,  0.061, 1.415],  14*pi/180 % shoulder.r
        [-0.196,  0.061, 1.121], 183*pi/180 % upperarm.r
        [-0.216, -0.031, 0.872], 181*pi/180 % lowerarm.r
        [-0.220, -0.044, 0.782],  92*pi/180 % hand.r
        [-0.222, -0.045, 0.694],  90*pi/180 % fingers.r
    ];

endfunction
