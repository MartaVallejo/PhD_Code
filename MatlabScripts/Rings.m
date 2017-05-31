%% Initialization
clear ; close all; clc

%% Load Data

E = load('Rings.txt');

X = E(1,:);
steps = [1:1:size(X,2)];

plot(steps, X)
hold on;
xlabel('rings')
ylabel('avgPrice')
title('Evolution of the population','FontSize',12)
handle=gcf;
saveas(handle, ['rings', 'png'],'png')
hold off;