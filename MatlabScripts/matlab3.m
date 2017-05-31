list = dir('../../PhD/CollectedData/Scenarios/2Chapter/Scenario 13/NEW');
isub = [list(:).isdir]; %# returns logical vector
nameFolds = {list(isub).name}'; % return name folders in scenario

cd('../../PhD/CollectedData/Scenarios/2Chapter/Scenario 13/NEW');
% Vectors one per each column of satisfaction files
S=[]; %Satisfaction achieved
G=[]; %Green areas
C=[]; % Closeness
B=[]; % Budget
E=[]; % Evolution
for i=3:size(nameFolds,1)
    cd(char([nameFolds(i)]));
    ofSat=dir(fullfile(['SatisfactionGA' '*' '.txt']));
    onSat=dir(fullfile(['SatisfactionMO' '*' '.txt']));
    j=1;
%     Offline Satisfaction
    while (length(ofSat)>=j)
        if not(isempty(ofSat(j).name))
            disp('OF Satisfaction')
            fileID=fopen(ofSat(j).name);
            % Long Version
            %A = textscan(fileID,'%d %d %d %d %d %d %f %f %f %d %d %f %d %d %d %f %s %d');
            % Short Version
            A = textscan(fileID,'%d %d %d %d %d %f %f %f %d %d %f %d %d %d %f %s %d');
            fclose(fileID);
            try
                S=[S; [1 0 A{2}']];
            catch err
                if (strcmp(err.identifier,'MATLAB:catenate:dimensionMismatch'))
                    disp('Dimensions of matrices being concatenated are not consistent in OF Satisfaction in folder:')
                    disp(ofSat(j).name)
                    cd('../../../../../../../MATLAB/General');
                end;
            end;
            % Long Version
            % G=[G; [1 A{6}']];           
            %C=[C; [1 A{16}']];
            %B=[B; [1 A{18}']];
            % Short Version
            G=[G; [1 A{5}']];
            C=[C; [1 A{15}']];
            B=[B; [1 A{17}']];
%           pause
        end;
        j=j+1;
    end;
%     Online Satisfaction
    j=1;
    while (length(onSat)>=j)
        if not(isempty(onSat(j).name))
            disp('ON Satisfaction')
            fileID=fopen(onSat(j).name);
            A = textscan(fileID,'%u %u %u %u %u %u %f %f %f %d %d %f %d %d %d %f %s %d %d %d %d %d %d %f %f %f %d');
            fclose(fileID);
            try
                S=[S; [0 0 A{2}']];
            catch err
                if (strcmp(err.identifier,'MATLAB:catenate:dimensionMismatch'))
                    disp('Dimensions of matrices being concatenated are not consistent in ON Satisfaction in folder:')
                    size(S)
                    size(A{2}')
                    cd('../../../../../../../MATLAB/General');
                end;
            end
            G=[G; [0 A{6}']];
            C=[C; [0 A{16}']];
            B=[B; [0 A{18}']];
            E=A{27}';
%           pause
        end;
        j=j+1;
    end;   
    cd('..');
end;
cd('../../../../../../MATLAB/General');
dlmwrite('satisfaction.txt', S);
dlmwrite('green.txt', G);
dlmwrite('closeness.txt', C);
dlmwrite('budgetONOF.txt', B);
dlmwrite('evolution.txt', E);
% pwd

% First column 0:Random, 1:GA