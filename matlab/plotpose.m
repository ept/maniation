function retval = plotpose(result)

    fixedbodies = 1;

  % restquat = [        % for articulated.xml
  %     -0.27060    -0.65328    0.65328     0.27060     0   % Bone1
  %     -0.70711    0.70711     0           0           1   % Bone2
  % ]'; %                                               ^^----- index of parent

    restquat = [        % for alfred
        +0.000000   -1.000000   +0.000000   +0.000000     0   % Base          1
        -0.705668   +0.708543   +0.000000   +0.000000     1   % Spine1        2
        +0.999998   +0.002033   +0.000000   +0.000000     2   % Spine2        3
        +0.758518   -0.082127   +0.090699   -0.640062     3   % Torso.L       4
        +0.688029   +0.111411   -0.715873   -0.041582     4   % Shoulder.L    5
        +0.456320   -0.533975   +0.422327   -0.572959     5   % UpperArm.L    6
        +0.992472   +0.103480   +0.060089   +0.026080     6   % LowerArm.L    7
        +0.740906   -0.039111   +0.667370   -0.064384     7   % Hand.L        8
        +0.997782   +0.027769   -0.003558   -0.060390     8   % Fingers.L     9
        +0.760386   -0.080166   -0.092908   +0.637773     3   % Torso.R       10
        +0.683878   +0.118400   +0.716630   +0.068798     10  % Shoulder.R    11
        -0.455903   +0.555092   +0.438619   -0.540035     11  % UpperArm.R    12
        +0.983891   +0.173211   -0.038090   -0.022496     12  % LowerArm.R    13
        +0.724694   -0.057346   -0.681136   +0.087082     13  % Hand.R        14
        +0.997762   +0.011300   +0.002952   +0.065843     14  % Fingers.R     15
        +0.993830   -0.110917   +0.000000   +0.000000     3   % Torso         16
        +0.929278   +0.369382   +0.000000   +0.000000     16  % Head          17
        +0.142554   +0.771972   +0.608627   +0.115372     1   % Hip.L         18
        +0.619809   -0.484401   -0.526539   +0.322413     18  % UpperLeg.L    19
        +0.996330   +0.085295   -0.001189   -0.007008     19  % LowerLeg.L    20
        +0.776633   -0.629017   -0.025452   -0.023065     20  % Foot.L        21
        +0.144452   +0.765307   -0.616731   -0.114376     1   % Hip.R         22
        +0.615563   -0.473360   +0.535985   -0.331259     22  % UpperLeg.R    23
        +0.996558   +0.082870   -0.001220   +0.001702     23  % LowerLeg.R    24
        +0.778609   -0.626686   +0.025367   +0.019722     24  % Foot.R        25
    ]';

  % restquat = [        % for rope
  %     0.7071068   0.7071068   0   0   0
  %     1           0           0   0   1
  %     1           0           0   0   2
  %     1           0           0   0   3
  %     1           0           0   0   4
  %     1           0           0   0   5
  %     1           0           0   0   6
  %     1           0           0   0   7
  %     1           0           0   0   8
  %     1           0           0   0   9
  %     1           0           0   0  10
  %     1           0           0   0  11
  %     1           0           0   0  12
  %     1           0           0   0  13
  %     1           0           0   0  14
  %     1           0           0   0  15
  %     1           0           0   0  16
  %     1           0           0   0  17
  %     1           0           0   0  18
  %     1           0           0   0  19
  %     1           0           0   0  20
  %     1           0           0   0  21
  %     1           0           0   0  22
  %     1           0           0   0  23
  %     1           0           0   0  24
  % ]';

    inRowsPerSkel = 13*columns(restquat);
    outRowsPerSkel = 3+4*columns(restquat);
    skelCount = (rows(result) - 1 - 13*fixedbodies)/inRowsPerSkel;
    data = zeros(columns(result), 1 + skelCount*outRowsPerSkel);
    data(:, 1) = result'(:, 1);
    for skel = 1:skelCount
        x = 13*fixedbodies + (skel-1)*inRowsPerSkel + 2;
        y = (skel-1)*outRowsPerSkel + 5;
        data(:, y-3:y-1) = result'(:, x:x+2);
        for t=1:columns(result)
            for b=1:columns(restquat)
                xx = x + 13*(b-1);
                q1 = result(xx+3:xx+6, t);
                p = restquat(5, b);
                if (p == 0) q0 = [1;0;0;0]; else
                    xp = x + 13*(p-1);
                    q0 = result(xp+3:xp+6, t);
                endif
                r = restquat(1:4, b);
                yy = y + 4*(b-1);
                data(t, yy:yy+3) = qmult(qinv(r), qmult(qinv(q0), q1))';
            endfor
        endfor
    endfor
    
    save -text blenderdata data
    
endfunction
