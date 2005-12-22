function retval = plotblender(result)
    % Write the results of a simulation to a file called `blenderdata'
    % which can be imported into blender.
    % The file format is a text file. Each line contains 6n numbers, where n is
    % the number of bodies. For each body, the position of the centre of mass (x,y,z)
    % and the three euler angles (psi, theta, phi) of the roll/pitch/yaw convention
    % are given in that order. The next line contains the values for the next time step.
    
    data = zeros(columns(result), 6*rows(result)/13);
    for n=1:rows(result)/13
        data(:, 6*n-5:6*n-3) = result'(:, 13*n-12:13*n-10);
        px = py = pz = 0;
        for m=1:columns(result)
            [rx, ry, rz] = qtoeuler(result(13*n-9:13*n-6, m));
            while (rx - px > pi) rx = rx - 2*pi; end
            while (px - rx > pi) rx = rx + 2*pi; end
            while (ry - py > pi) ry = ry - 2*pi; end
            while (py - ry > pi) ry = ry + 2*pi; end
            while (rz - pz > pi) rz = rz - 2*pi; end
            while (pz - rz > pi) rz = rz + 2*pi; end
            data(m, 6*n-2:6*n) = [rx, ry, rz];
            px = rx; py = ry; pz = rz;
        endfor
    endfor
    data = real(data);
    save -ascii blenderdata data
endfunction
