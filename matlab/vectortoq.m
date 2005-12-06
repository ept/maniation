function quat = vectortoq(original, transformed, roll)
    % Create a quaternion which transformes one vector into another
    % (considering only their direction, not magnitude).
    % Roll is the angle in radians by which a rotation should occur
    % around the axis of the vector.

    original = normalize(original);
    transformed = normalize(transformed);
    angle = acos(original'*transformed);

    rollq = qrot(original, roll);
    
    rot = [0; 0; 0; 1];
    if (abs(angle) > 0.000001)
        rot = qrot(cross(original, transformed), -angle);
    endif

    quat = qmult(rollq, rot);
endfunction
