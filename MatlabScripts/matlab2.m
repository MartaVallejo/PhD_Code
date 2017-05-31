%list = dir('../../../Documents/PhD/CollectedData/Scenarios/Thesis/Scenario 10b/NEW');
list = dir('../../HW/PhD/CollectedData/Scenarios/1Chapter/Scenario 6d/ICCS');
isub = [list(:).isdir]; %# returns logical vector
nameFolds = {list(isub).name}'; % return name folders in scenario

%cd('../../../Documents/PhD/CollectedData/Scenarios/Thesis/Scenario 10b/NEW');
cd('../../HW/PhD/CollectedData/Scenarios/1Chapter/Scenario 6d/ICCS');
% Vectors one per each column of satisfaction files
S=[]; %Satisfaction achieved
P=[]; %Population
U=[]; %Urbanised areas
G=[]; %Green areas
M=[]; %Migration
MinG=[]; % Min Green prices
MaxG=[]; % Max Green prices
MinU=[]; % Min Urban prices
MaxU=[]; % Max Urban prices
AvgG=[]; % Avg Green prices
AvgU=[]; % Avg Urban prices
C=[]; % Closeness
RB=[]; % Remaining Budget
for i=3:size(nameFolds,1)
    cd(char([nameFolds(i)]));
    % fullfile: Build full filename from parts
    gaSat=dir(fullfile(['SatisfactionGA' '*' '.txt']));
    ranSat=dir(fullfile(['SatisfactionRan' '*' '.txt']));
    cloSat=dir(fullfile(['SatisfactionClo' '*' '.txt']));
    j=1;
%     GA Satisfaction
    while (length(gaSat)>=j)
        if not(isempty(gaSat(j).name))
            fileID=fopen(gaSat(j).name);
            A = textscan(fileID,'%d %d %d %d %d %f %f %f %d %d %f %d %d %d %f %s %d');
            fclose(fileID);
            try
                %disp('GA Satisfaction')
                S=[S;  [1 str2num(nameFolds{i}) A{2}']];
            catch err
                if (strcmp(err.identifier,'MATLAB:catenate:dimensionMismatch'))
                    disp('Dimensions of matrices being concatenated are not consistent in GA Satisfaction in folder:')
                    nameFolds{i}
                    % With NEW folder
                    pwd
                    cd('../../../../../../../MATLAB/Calculus/General');
                    %cd('../../../../../../MATLAB/Calculus/General');
                end;
            end;
            P=[P; [1 A{1}']];
            U=[U; [1 A{4}']];
            G=[G; [1 A{5}']];
            MinG=[MinG; [[1 A{6}']]];
            MaxG=[MaxG; [[1 A{7}']]];
            AvgG=[AvgG; [[1 A{8}']]];
            MinU=[MinU; [[1 A{9}']]];
            MaxU=[MaxU; [[1 A{10}']]];        
            AvgU=[AvgU; [[1 A{11}']]];
            M=[M; [1 A{14}']];
            C=[C; [1 A{15}']];
            RB=[RB; [1 A{17}']];
%           pause
        end;
        j=j+1;
    end;
%     Random Satisfaction
    j=1;
    while (length(ranSat)>=j)
        if not(isempty(ranSat(j).name))
            fileID=fopen(ranSat(j).name);
            A = textscan(fileID,'%d %d %d %d %d %f %f %f %d %d %f %d %d %d %f %s %d');
            fclose(fileID);
            %disp('RANDOM Satisfaction')
            S=[S; [0 0 A{2}']];
            P=[P; [0 A{1}']];
            U=[U; [0 A{4}']];
            G=[G; [0 A{5}']];
            MinG=[MinG; [[0 A{6}']]];
            MaxG=[MaxG; [[0 A{7}']]];
            AvgG=[AvgG; [[0 A{8}']]];
            MinU=[MinU; [[0 A{9}']]];
            MaxU=[MaxU; [[0 A{10}']]];        
            AvgU=[AvgU; [[0 A{11}']]];
            M=[M; [0 A{14}']];
            C=[C; [0 A{15}']];
            RB=[RB; [0 A{17}']];
%           pause
        end;
        j=j+1;
    end;
%     Closest Satisfaction
    j=1;
    while (length(cloSat)>=j)
        if not(isempty(cloSat(j).name))
            fileID=fopen(cloSat(j).name);
            A = textscan(fileID,'%d %d %d %d %d %f %f %f %d %d %f %d %d %d %f %s %d');
            fclose(fileID);
            try
                S=[S; [2 0 A{2}']];
            catch err
                if (strcmp(err.identifier,'MATLAB:catenate:dimensionMismatch'))
                    disp('Dimensions of matrices being concatenated are not consistent in CLO Satisfaction.')
                    cd('../../../../../../../MATLAB/Calculus/Functions');
                    %cd('../../../../../../MATLAB/Calculus/General');
                end;
            end;
            P=[P; [2 A{1}']];
            U=[U; [2 A{4}']];
            G=[G; [2 A{5}']];
            MinG=[MinG; [[2 A{6}']]];
            MaxG=[MaxG; [[2 A{7}']]];
            AvgG=[AvgG; [[2 A{8}']]];
            MinU=[MinU; [[2 A{9}']]];
            MaxU=[MaxU; [[2 A{10}']]];        
            AvgU=[AvgU; [[2 A{11}']]];
            M=[M; [2 A{14}']];
            C=[C; [2 A{15}']];
            RB=[RB; [2 A{17}']];
%           pause
        end;
        j=j+1;
    end;   
    cd('..');
end;
% With NEW folder
%cd('../../../../../../../MATLAB/Calculus/General');
cd('../../../../../../../MATLAB/General');
dlmwrite('satisfaction.txt', S);
dlmwrite('population.txt', P);
dlmwrite('urbanised.txt', U);
dlmwrite('green.txt', G);
dlmwrite('migration.txt', M);
dlmwrite('minGreenPrice.txt', MinG);
dlmwrite('maxGreenPrice.txt', MaxG);
dlmwrite('minUrbanPrice.txt', MinU);
dlmwrite('maxUrbanPrice.txt', MaxU);
dlmwrite('avgGreenPrice.txt', AvgG);
dlmwrite('avgUrbanPrice.txt', AvgU);
dlmwrite('closeness.txt', C);
dlmwrite('remainBudget.txt', RB);
% pwd

% First column 0:Random, 1:GA
% Second column number of simulations. 0 for random
% Rest values the satisfaction in each tick of the clock
