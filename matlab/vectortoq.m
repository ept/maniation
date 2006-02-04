function quat = vectortoq(original, transformed, roll)
    % Create a quaternion which transformes one vector into another
    % (considering only their direction, not magnitude).
    % Roll is the angle in radians by which a rotation should occur
    % around the axis of the vector.

    original = normalize(original);
    transformed = normalize(transformed);
    angle = acos(original'*transformed);

    rollq = qrot(original, roll);
    
    rot = [1; 0; 0; 0];
    axis = cross(original, transformed);
    
    if (sumsq(axis) > 0.0000000000001)
        rot = qrot(axis, angle);
    else
        if (original'*transformed < 0)
            % need to reverse direction
            rot = qrot(anyortho(original), pi);
        endif
    endif

    quat = qmult(rot, rollq);
endfunction
