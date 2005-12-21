function angle = qtoangle(quat, zero, normal)
    % Rotate vector `zero' by quaternion `quat'; project it onto the plane whose
    % normal vector is `normal'; return the angle in radians (between -pi and pi)
    % by which the quaternion rotated the vector.
    
    zero = normalize(zero);
    normal = normalize(normal);
    new = qtransform(quat, zero);
    new = new - (new'*normal)*normal;
    angle = acos(zero'*new);
    if (new'*cross(normal, zero) < 0) angle = -angle; endif
endfunction
