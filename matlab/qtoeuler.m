function [rotX, rotY, rotZ] = qtoeuler(quat)
    % uses the roll/pitch/yaw convention (Shoemake:85).
    
    m = qtomatrix(quat);
    sy = -m(1,3);
    cy = sqrt(1 - sy^2);
    if (abs(cy) < 1e-6)
        sx = -m(3,2);
        cx = m(2,2);
        sz = 0;
        cz = 1;
    else
        sx = m(2,3)/cy;
        cx = m(3,3)/cy;
        sz = m(1,2)/cy;
        cz = m(1,1)/cy;
    end

    rotX = acos(cx); if (sx < 0) rotX = 2*pi - rotX; end
    rotY = acos(cy); if (sy < 0) rotY = 2*pi - rotY; end
    rotZ = acos(cz); if (sz < 0) rotZ = 2*pi - rotZ; end
endfunction
