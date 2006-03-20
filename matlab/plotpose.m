function retval = plotpose(result)

    fixedbodies = 1;
    restquat = [
        0.70711 0.5     0       0.5         0   % Bone1
        0.5     0       0.5     -0.70711    1   % Bone2
    ]'; %                                   ^^----- index of parent

    data = zeros(columns(result), 4*((rows(result)-1)/13 - fixedbodies) + 4);
    data(:, 1) = result'(:, 1);
    x = 13*fixedbodies+2;
    data(:, 2:4) = result'(:, x:x+2);
    for t=1:columns(result)
        for b=1:((rows(result)-1)/13 - fixedbodies)
            x = 13*(fixedbodies+b)-8;
            q1 = result(x:x+3, t);
            p = restquat(5, b);
            if (p == 0) q0 = [1;0;0;0]; else
                x = 13*(fixedbodies+p)-8;
                q0 = result(x:x+3, t);
            endif
            r = restquat(1:4, b);
            data(t, 4*b+1:4*b+4) = qmult(qinv(r), qmult(qinv(q0), q1))';
        endfor
    endfor
    
    save -text blenderdata data
    
endfunction
